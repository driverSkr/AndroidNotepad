package com.ethan.mediapicker

import androidx.core.content.FileProvider


class MediaPickerFileProvide : FileProvider() {
    override fun onCreate(): Boolean {
        return true
    }
}
