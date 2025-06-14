package com.ethan.file

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


private const val TAG = "PhotoAlbumUtils"

/**
 * @author  PengHaiChen
 * @date    2024/2/1 14:51
 * @email   penghaichen@tenorshare.cn
 */
object GalleryUtils {

    suspend fun add2Gallery(
        context: Context,
        dir: String,
        videoFileName: String,
        videoFile: File,
    ) = suspendCoroutine { suspendCoroutine ->
        val values = ContentValues()
        val uriSavedVideo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/${dir}")
            values.put(MediaStore.Video.Media.TITLE, videoFileName)
            values.put(MediaStore.Video.Media.DISPLAY_NAME, videoFileName)
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            context.contentResolver.insert(collection, values)
        } else {
            values.put(MediaStore.Video.Media.TITLE, videoFileName)
            values.put(MediaStore.Video.Media.DISPLAY_NAME, videoFileName)
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        }
        val pfd: ParcelFileDescriptor?
        try {
            pfd = context.contentResolver.openFileDescriptor(uriSavedVideo!!, "w")
            val out = FileOutputStream(pfd!!.fileDescriptor)
            val ins = FileInputStream(videoFile)
            val buf = ByteArray(8192)
            var len: Int
            while (ins.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            out.close()
            ins.close()
            pfd.close()
        } catch (e: Exception) {
            suspendCoroutine.resume(false)
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)
            uriSavedVideo?.let { context.contentResolver.update(it, values, null, null) }
        }
        suspendCoroutine.resume(true)
    }
}