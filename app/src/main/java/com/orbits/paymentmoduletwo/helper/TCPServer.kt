package com.orbits.paymentmoduletwo.helper

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.orbits.paymentmoduletwo.interfaces.MessageListener
import io.nearpay.sdk.utils.enums.TransactionData
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.security.MessageDigest
import java.util.*
import kotlin.collections.HashMap
import kotlin.experimental.xor

class TCPServer(private val port: Int, private val messageListener: MessageListener) {

    private var serverSocket: ServerSocket? = null
    private val clients = HashMap<String, ClientHandler>()
    private val connectedClientsList = MutableLiveData<List<String>>()

    init {
        connectedClientsList.value = emptyList()
    }

    fun observeClientList(): LiveData<List<String>> {
        return connectedClientsList
    }

    fun start() {
        try {
            serverSocket = ServerSocket(port)
            println("TCP Server started on port $port")
            while (true) {
                val clientSocket = serverSocket?.accept()
                val clientHandler = ClientHandler(clientSocket)
                println("Client connected: ${clientSocket?.inetAddress}")
                // Handle client connection on a new thread
                if (!clients.containsKey(clientHandler.clientId)) {
                    clients[clientHandler.clientId] = clientHandler
                    messageListener.onClientConnected(clientSocket)
                    Thread(clientHandler).start()

                    // Add client to connectedClientsList
                    addToConnectedClients(clientHandler.clientId)
                } else {
                    println("Client ${clientHandler.clientId} is already connected.")
                    // Optionally, you can notify or handle this scenario accordingly
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        serverSocket?.close()
        println("TCP Server stopped")
    }


    /*------------------------------------------------All Functions-----------------------------------------------------------------*/



    private fun addToConnectedClients(clientId: String) {
        synchronized(clients) {
            val currentList = connectedClientsList.value.orEmpty().toMutableList()
            currentList.add(clientId)
            println("here is list 1111 $currentList")
            connectedClientsList.postValue(currentList)
        }
    }

    private fun removeFromConnectedClients(clientId: String) {
        synchronized(clients) {
            clients.remove(clientId)
            val currentList = connectedClientsList.value.orEmpty().toMutableList()
            currentList.remove(clientId)
            connectedClientsList.postValue(currentList)
        }
    }

    var counter = 1

    fun generateCustomId(): String {
        return counter++.toString()
    }

    fun sendMessageToClient(recipientClientId: String, message: TransactionData) {
        Thread {
            synchronized(clients) {
                val recipientClientHandler = clients[recipientClientId]
                if (recipientClientHandler != null) {
                    try {
                        recipientClientHandler.outStream?.write(message.toString().toByteArray())
                        println("Sent message to client $recipientClientId: $message")
                    } catch(e: Exception) {
                        println("Error sending message to client $recipientClientId: ${e.message}")
                    }
                } else {
                    println("Recipient client $recipientClientId not found or not connected.")
                }
            }
        }.start()
    }

    fun sendMessageToClientString(recipientClientId: String, message: String) {
        Thread {
            synchronized(clients) {
                val recipientClientHandler = clients[recipientClientId]
                if (recipientClientHandler != null) {
                    try {
                        recipientClientHandler.outStream?.write(message.toByteArray())
                        println("Sent message to client $recipientClientId: $message")
                    } catch(e: Exception) {
                        println("Error sending message to client $recipientClientId: ${e.message}")
                    }
                } else {
                    println("Recipient client $recipientClientId not found or not connected.")
                }
            }
        }.start()
    }


    /*------------------------------------------------All Functions-----------------------------------------------------------------*/


    //----------------------------------------------------------------------//----------------------------------------------------------------------------//


    inner class ClientHandler(val clientSocket: Socket?) : Runnable {
        private var inStream: BufferedReader? = null
        var outStream: OutputStream? = null
        var isWebSocket = false
        val clientId = generateCustomId() // Generate a unique client identifier

        init {
            try {
                WebSocketManager.addClientHandler(clientId, this)
                inStream = BufferedReader(InputStreamReader(clientSocket?.getInputStream()))
                outStream = clientSocket?.getOutputStream()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            try {
                // Perform WebSocket handshake if applicable
                if (performHandshake()) {
                    isWebSocket = true
                    println("WebSocket handshake successful for client $clientId")

                    while (true) {
                        val message = readWebSocketFrame(clientSocket?.getInputStream() ?: return)
                        if (message.isNullOrEmpty()) break

                        println("Received WebSocket message from client $clientId: $message")

                        try {
                            println("Received WebSocket jsonObject from client $clientId: $message")
                            val jsonObject = Gson().fromJson(message, JsonObject::class.java)
                            messageListener.onMessageJsonReceived(jsonObject)

                            /*if (!jsonObject.isJsonNull){
                                if (jsonObject.get("amount").asString.isNotEmpty()){
                                    handleMessage(clientId,"Payment Initiated")
                                }
                            }*/

                        } catch (e: JsonSyntaxException) {
                            println("Invalid JSON format received from client $clientId: $message")
                            inStream?.close()
                            outStream?.close()
                            clientSocket.close()
                        }

                         outStream?.flush()
                    }
                } else {
                    // Handle TCP communication
                    var message: String?
                    while (inStream?.readLine().also { message = it } != null) {
                        println("Received from TCP client $clientId: $message")
                        try {
                            println("Received WebSocket jsonObject from client $clientId: $message")
                            val jsonObject = Gson().fromJson(message, JsonObject::class.java)
                            messageListener.onMessageJsonReceived(jsonObject)
                        } catch (e: JsonSyntaxException) {
                            println("Invalid JSON format received from client $clientId: $message")
                            inStream?.close()
                            outStream?.close()
                            clientSocket?.close()
                        }
                        outStream?.flush()

                        // Handle TCP message here as needed
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    inStream?.close()
                    outStream?.close()
                    clientSocket?.close()
                    clients.remove(clientId)
                    removeFromConnectedClients(clientId)
                    println("Client disconnected: $clientId")
                    messageListener.onClientDisconnected()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun performHandshake(): Boolean {
            try {
                val request = readHttpRequest()
                val webSocketKey = extractWebSocketKey(request)
                if (webSocketKey.isNotEmpty()) {
                    val acceptKey = generateWebSocketAcceptKey(webSocketKey)
                    val response = buildHandshakeResponse(acceptKey)
                    outStream?.write(response.toByteArray())
                    outStream?.flush()
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

        private fun readHttpRequest(): String {
            val requestBuilder = StringBuilder()
            var line: String?
            while (inStream?.readLine().also { line = it } != null && line != "") {
                requestBuilder.append(line).append("\r\n")
            }
            requestBuilder.append("\r\n")
            return requestBuilder.toString()
        }

        private fun extractWebSocketKey(request: String): String {
            val keyStart = request.indexOf("Sec-WebSocket-Key: ") + 19
            val keyEnd = request.indexOf("\r\n", keyStart)
            return request.substring(keyStart, keyEnd).trim()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun generateWebSocketAcceptKey(webSocketKey: String): String {
            val magicKey = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
            val combined = webSocketKey + magicKey
            val bytes = MessageDigest.getInstance("SHA-1").digest(combined.toByteArray())
            return Base64.getEncoder().encodeToString(bytes)
        }

        private fun buildHandshakeResponse(acceptKey: String): String {
            return """
                HTTP/1.1 101 Switching Protocols
                Upgrade: websocket
                Connection: Upgrade
                Sec-WebSocket-Accept: $acceptKey
                """.trimIndent() + "\r\n\r\n"
        }

        fun handleMessage(clientId: String,message: String) {
            sendMessageToClient(clientId, message)
        }

        fun sendMessageToClient(clientId: String?, message: String) {
            val response = encodeWebSocketFrame(message)
            val recipient = clients[clientId]
            if (isWebSocket) {
                println("here is recipient 111 $recipient")
                if (clientId != null) {
                    try {
                        recipient?.outStream?.write(response)
                        recipient?.outStream?.flush()
                        println("Sent message to client $clientId: $message")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    println("Recipient Websocket client $clientId not found or not connected.")
                    // Optionally handle this scenario (e.g., notify sender)
                }
            }else {
                if (recipient != null) {
                    try {
                        // Assuming `outStream` is the OutputStream for the TCP client
                        val messageContent = message.substringAfter(":").trim()
                        recipient.outStream?.write(response)
                        recipient.outStream?.flush()
                        println("Sent message to TCP client $clientId:$message")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    println("Recipient client $clientId not found or not connected.")
                    // Optionally handle this scenario (e.g., notify sender)
                }
            }

        }

        // WebSocket frame encoding function
        private fun encodeWebSocketFrame(message: String): ByteArray {
            val rawData = message.toByteArray(Charsets.UTF_8)
            val frame = ByteArrayOutputStream()

            // FIN, RSV1, RSV2, RSV3 flags (1 byte)
            frame.write(0x81) // FIN + Opcode (0x1 for text frame)

            // Payload length
            if (rawData.size <= 125) {
                frame.write(rawData.size)
            } else if (rawData.size <= 65535) {
                frame.write(126)
                frame.write(rawData.size shr 8)
                frame.write(rawData.size and 0xFF)
            } else {
                frame.write(127)
                for (i in 7 downTo 0) {
                    frame.write((rawData.size shr (i * 8)) and 0xFF)
                }
            }

            // Payload data
            frame.write(rawData)

            return frame.toByteArray()
        }


        // WebSocket frame decoding function
        private fun readWebSocketFrame(input: InputStream): String? {
            val firstByte = input.read()
            val secondByte = input.read()
            if (firstByte == -1 || secondByte == -1) return null

            val isMasked = (secondByte and 0x80) != 0
            var payloadLength = secondByte and 0x7F

            if (payloadLength == 126) {
                payloadLength = (input.read() shl 8) or input.read()
            } else if (payloadLength == 127) {
                payloadLength = 0
                for (i in 0..7) {
                    payloadLength = (payloadLength shl 8) or ((input.read().toLong() and 0xFF).toInt())
                }
            }

            val maskingKey = if (isMasked) ByteArray(4) { input.read().toByte() } else null
            val payload = ByteArray(payloadLength)

            for (i in payload.indices) {
                val byte = input.read().toByte()
                payload[i] = if (isMasked) (byte xor maskingKey!![i % 4]) else byte
            }

            return String(payload, Charsets.UTF_8)
        }
    }

    object WebSocketManager {
        private val clientHandlers: MutableMap<String, ClientHandler> = mutableMapOf()

        fun addClientHandler(clientId: String, clientHandler: ClientHandler) {
            clientHandlers[clientId] = clientHandler
        }

        fun getClientHandler(clientId: String): ClientHandler? {
            return clientHandlers[clientId]
        }

        fun removeClientHandler(clientId: String) {
            clientHandlers.remove(clientId)
        }
    }
}