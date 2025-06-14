package com.ethan.android.notepad.base

import com.ethan.android.notepad.NotepadApp

object Constants {

    private val DATA_PATH = NotepadApp.INSTANCE?.getExternalFilesDir(null)?.absolutePath
    val PATH_IMAGE = "$DATA_PATH/Image/"
    val PATH_LOG = "$DATA_PATH/LOG/"
    val PATH_CACHE = "$DATA_PATH/Cache/"

    const val TERMS_OF_USE = "https://www.niuxuezhang.cn/app-html5/hitpaw-video-enhancer-app-terms-and-conditions.html"
    const val PRIVACY_POLICY = "https://www.niuxuezhang.cn/app-html5/hitpaw-video-enhancer-app-privacy-policy.html"
}