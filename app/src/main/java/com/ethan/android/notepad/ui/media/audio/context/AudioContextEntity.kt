package com.ethan.android.notepad.ui.media.audio.context

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AudioContextEntity {
    var currentView by mutableStateOf(ViewType.Record)
    var originPath by mutableStateOf("")
    var finalPath by mutableStateOf("")
}

val LocalAudioContextEntity = compositionLocalOf {
    AudioContextEntity()
}

enum class ViewType{
    Record,
    Cutting
}