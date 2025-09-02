package com.ethan.android.notepad.ui.media.audio.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.common.view.TitleCardView
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.ui.media.audio.context.LocalAudioContextEntity
import com.ethan.android.notepad.ui.media.audio.context.ViewType
import com.ethan.android.notepad.ui.media.audio.view.AudioCuttingView
import com.ethan.android.notepad.ui.media.audio.view.AudioRecordView

@Composable
@Preview
fun AudioRecordAudioTrackPage() {

    val localAudio = LocalAudioContextEntity.current

    AnimatedContent(localAudio.currentView, modifier = Modifier.fillMaxSize().padding(vertical = 30.dp)) {
        TitleCardView(
            title = if (it == ViewType.Record) "录音" else "裁剪",
            modifier = Modifier.background(color = Black).padding(horizontal = 10.dp, vertical = 15.dp)
        ) {
            when(it) {
                ViewType.Record -> AudioRecordView()
                ViewType.Cutting -> AudioCuttingView(localAudio.originPath, localAudio.finalPath)
            }
        }
    }
}