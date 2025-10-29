package com.ethan.android.notepad.common.model.download

import java.io.File

data class DownloadResult(
    val file: File? = null,
    val error: String? = null,
    val isCompleted: Boolean = false
)
