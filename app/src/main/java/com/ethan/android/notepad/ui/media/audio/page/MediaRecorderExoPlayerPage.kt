package com.ethan.android.notepad.ui.media.audio.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ethan.android.notepad.theme.White

@Composable
@Preview
fun MediaRecorderExoPlayerPage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "MediaRecorder 录音 + ExoPlayer  播放",
            color = White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}