package com.ethan.android.notepad.common.model.download

data class DownloadProgress(
    val currentBytes: Long,
    val totalBytes: Long,
    val progress: Float,
    val isDownloading: Boolean
)
