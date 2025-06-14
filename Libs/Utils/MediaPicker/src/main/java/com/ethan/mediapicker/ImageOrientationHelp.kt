package com.ethan.mediapicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author  PengHaiChen
 * @date    2024/1/30 9:44
 * @email   penghaichen@tenorshare.cn
 */
object ImageOrientationHelp {
    suspend fun fitImageOrientation(context: Context, imgPath: String): String = suspendCoroutine { suspendCoroutine ->
        if (imgPath.isEmpty()) {
            suspendCoroutine.resume("")
            return@suspendCoroutine
        }
        val bm = BitmapFactory.decodeFile(imgPath)
        var info = 0
        val exif: ExifInterface? = try {
            ExifInterface(imgPath)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        var isXTransverse = false
        var isYTransverse = false
        if (exif != null) { // 读取图片中相机方向信息
            val ori: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            info = when (ori) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    isYTransverse = true
                    90
                }

                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    isXTransverse = true
                    270
                }

                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                    isXTransverse = true
                    0
                }

                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    isYTransverse = true
                    0
                }

                else -> {
                    suspendCoroutine.resume(imgPath)
                    return@suspendCoroutine
                }
            }
            val m = Matrix()
            m.setScale(if (isXTransverse) -1F else 1F, if (isYTransverse) -1F else 1F)
            m.postRotate(info.toFloat())
            val fixBitmap = Bitmap.createBitmap(bm!!, 0, 0, bm.width, bm.height, m, true)
            val imagePathDir = "${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)}/orientation_fix"
            File(imagePathDir).mkdir()
            val imagePath = File("${imagePathDir}/${UUID.randomUUID()}.${File(imgPath).extension}").apply { this.createNewFile() }
            val coverPath = fixBitmap.let {
                if (it == null) return@let imgPath
                val fileOutputStream = FileOutputStream(imagePath)
                it.compress(when (File(imgPath).extension.uppercase()) {
                    "JPEG|JPG" -> Bitmap.CompressFormat.JPEG
                    "PNG" -> Bitmap.CompressFormat.PNG
                    else -> Bitmap.CompressFormat.JPEG
                }, 100, fileOutputStream)
                fileOutputStream.close()
                it.recycle()
                imagePath.absolutePath
            }
            suspendCoroutine.resume(coverPath)
            return@suspendCoroutine
        } else {
            suspendCoroutine.resume(imgPath)
            return@suspendCoroutine
        }
    }
}