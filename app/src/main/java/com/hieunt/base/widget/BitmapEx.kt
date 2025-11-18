package com.hieunt.base.widget

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

suspend fun Bitmap.saveToFile(filePath: String, maxSize: Int? = null): Boolean = withContext(Dispatchers.IO) {
    return@withContext try {
        // Resize bitmap if maxSize is provided
        val resizedBitmap = if (maxSize != null) {
            val ratio = width.toFloat() / height.toFloat()
            val targetWidth: Int
            val targetHeight: Int

            if (width >= height) {
                targetWidth = maxSize
                targetHeight = (maxSize / ratio).toInt()
            } else {
                targetHeight = maxSize
                targetWidth = (maxSize * ratio).toInt()
            }

            Bitmap.createScaledBitmap(this@saveToFile, targetWidth, targetHeight, true)
        } else {
            this@saveToFile
        }

        FileOutputStream(filePath).use { out ->
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // Recycle resized bitmap if it's not the original
        if (resizedBitmap != this@saveToFile) {
            resizedBitmap.recycle()
        }

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}