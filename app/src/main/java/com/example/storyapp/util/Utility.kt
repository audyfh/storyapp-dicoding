package com.example.storyapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File

object Utility {

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)

        var compressQuality = 100
        var streamLength: Int
        do {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, stream)
            val byteArray = stream.toByteArray()
            streamLength = byteArray.size
            compressQuality -= 5
        } while (streamLength > 1000_000 && compressQuality > 5)

        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, it)
        }
        return file
    }

    fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = File.createTempFile("temp", null, context.cacheDir)
        inputStream.copyTo(file.outputStream())
        return file
    }
}