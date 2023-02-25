package com.jeckonly.storagedemo.storageutil

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

// example

//  1)  /data/data/com.xxx.xxx/...
//  2)  (首先 /sdcard/、/storage/self/primary/ 真正指向的是/storage/emulated/0/，所以用x表示这三者) x/Android/data/com.xxx.xxx/...


// 1)
suspend fun saveImageToFilesDir(context: Context, bitmap: Bitmap, filename: String): Boolean {
    val result = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, filename)
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.use {
            val result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            result
        }
    }
    return result
}

fun deleteImageFromFilesDir(context: Context, filename: String): Boolean {
    val file = File(context.filesDir, filename)
    return if (file.exists() and file.isFile) {
        file.delete()
    } else false
}

suspend fun loadImageFormFilesDir(context: Context, imageName: String): Bitmap? {
    val bitmap: Bitmap? =  withContext(Dispatchers.IO) {
        val file = File(context.filesDir, imageName)
        if (file.exists() and file.canRead() and file.isFile) {
            val bytes = file.readBytes()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Log.d("TAG", "bmp hashcode: ${bmp.hashCode()}")
            bmp
        } else {
            null
        }
    }
    return bitmap
}


// -----2)
/**
 * 使用[DownloadManager]下载一张图片到 x/Android/data/com.xxx.xxx/files/download 目录
 *
 * setDestinationInExternalFilesDir这一行的等价写法为
 *      setDestinationUri(Uri.fromFile(File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "image.jpg"))) 这个api的uri只能是external storage的路径
 */
fun downloadImageToExternalFilesDir(context: Context, filename: String, url: String = "https://i.pinimg.com/474x/ee/20/dc/ee20dc865f60372d5ade0b80b754f6d1.jpg"): Long {
    val downloadManager = context.getSystemService(DownloadManager::class.java)
    val request = DownloadManager.Request(url.toUri())
        .setMimeType("image/jpeg")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setTitle("image.jpg")
        .addRequestHeader("Authorization", "Bearer <token>")
        .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, filename)
    return downloadManager.enqueue(request)
}

suspend fun loadImageFormExternalFilesDir(context: Context, imageName: String): Bitmap? {
    val bitmap: Bitmap? =  withContext(Dispatchers.IO) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), imageName)
        if (file.exists() and file.canRead() and file.isFile) {
            val bytes = file.readBytes()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Log.d("TAG", "bmp hashcode: ${bmp.hashCode()}")
            bmp
        } else {
            null
        }
    }
    return bitmap
}

fun deleteImageFromExternalFilesDir(context: Context, filename: String): Boolean {
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)
    return if (file.exists() and file.isFile) {
        file.delete()
    } else false
}

suspend fun saveImageToExternalFilesDir(context: Context, bitmap: Bitmap, filename: String): Boolean {
    val result = withContext(Dispatchers.IO) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.use {
            val result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            result
        }
    }
    return result
}
