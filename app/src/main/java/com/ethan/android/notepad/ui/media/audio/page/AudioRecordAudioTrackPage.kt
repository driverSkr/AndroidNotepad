package com.ethan.android.notepad.ui.media.audio.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.ui.media.audio.view.AudioRecordView

@Composable
@Preview
fun AudioRecordAudioTrackPage() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AudioRecord 录音 + AudioTrack  播放",
            color = Black,
        )

        AudioRecordView()
    }
}