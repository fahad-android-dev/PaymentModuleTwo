package com.orbits.paymentmoduletwo.helper

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class WebSocketClient(port: Int) : WebSocketServer(InetSocketAddress(port)) {
    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        println("WebSocket connection opened: ${conn?.remoteSocketAddress}")
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        println("WebSocket connection closed: ${conn?.remoteSocketAddress}")
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        println("Received message from WebSocket client: $message")
        // Example: echo back the message
        conn?.send("Echo: $message")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        ex?.printStackTrace()
    }

    override fun onStart() {
        println("WebSocket Server started")
    }
}
