package com.orbits.paymentmoduletwo.helper

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


object Base64Converter {

    /** will convert any file to base 64*/
    fun convertImgToBase64(uri: Uri, activity: Activity): String {
        return try {
            val inputStream: InputStream? = activity.contentResolver.openInputStream(uri)
            val bytes = inputStream?.let { getBytes(it) }
            val imageEncoded = Base64.encodeToString(bytes, Base64.DEFAULT)
            inputStream?.close()
//            "data:image/jpeg;base64," + imageEncoded.replace(" ", "").replace("\n", "")
            imageEncoded.replace(" ", "").replace("\n", "")

        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun convertPdfToBase64(uri: Uri, activity: Activity): String {
        return try {
            val inputStream: InputStream? = activity.contentResolver.openInputStream(uri)
            val bytes = inputStream?.let { getBytes(it) }
            val imageEncoded = Base64.encodeToString(bytes, Base64.DEFAULT)
            inputStream?.close()
//            "data:application/pdf;base64," + imageEncoded.replace(" ", "").replace("\n", "")
            imageEncoded.replace(" ", "").replace("\n", "")
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun convertImgToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInByte = baos.toByteArray()
        val imageEncoded = Base64.encodeToString(imageInByte, Base64.DEFAULT)
        //return "data:image/jpeg;base64," + imageEncoded.replace(" ", "").replace("\n", "")
        return  imageEncoded.replace(" ", "").replace("\n", "")
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }
}