package com.ethan.android.notepad.common.utils

import android.media.MediaMetadataRetriever

object VideoHelper {

    fun getVideoInfo(filePath: String?): Long? {
        if (filePath == null) {
            return null
        }
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            return duration
        } catch (e: Exception) {
            return null
        } finally {
            retriever.release()
        }
    }

    fun getVideoInfoRotate(filePath: String?): Int? {
        if (filePath == null) {
            return null
        }
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            val rotate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
            return rotate
        } catch (e: Exception) {
            return null
        } finally {
            retriever.release()
        }
    }

    fun isHttpsOk(url: String): Boolean {
        return url.startsWith("https") || url.startsWith("http")
    }
}