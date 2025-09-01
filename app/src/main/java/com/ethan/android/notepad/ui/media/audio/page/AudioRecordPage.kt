package com.ethan.android.notepad.ui.media.audio.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ethan.android.notepad.common.model.CardItem
import com.ethan.android.notepad.common.view.ListCardView
import com.ethan.android.notepad.common.view.StatusBarsView
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.ui.media.audio.model.AudioEnum

@Composable
@Preview
fun AudioRecordPage() {

    //流程：录音裁剪集成在一个组件里
    var selectPage by remember { mutableStateOf(AudioEnum.AudioRecordAudioTrack) }
    val items = listOf(
        CardItem("AudioRecord 录音 + AudioTrack  播放", true) { selectPage =
            AudioEnum.AudioRecordAudioTrack
        },
        CardItem("MediaRecorder 录音 + MediaPlayer  播放", true) { selectPage =
            AudioEnum.MediaRecorderMediaPlayer
        },
        CardItem("MediaRecorder 录音 + ExoPlayer  播放", true) { selectPage =
            AudioEnum.MediaRecorderExoPlayer
        },
        CardItem("音频裁剪", true) { selectPage =
            AudioEnum.AudioCutting
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "录音")
        ListCardView(items)

        AnimatedContent(selectPage, label = "") {
            when(it) {
                AudioEnum.MediaRecorderMediaPlayer -> {
//                    MediaRecorderMediaPlayerPage()
                    Box(modifier = Modifier.fillMaxSize().background(color = Black))
                }
                AudioEnum.MediaRecorderExoPlayer -> MediaRecorderExoPlayerPage()
                AudioEnum.AudioRecordAudioTrack -> AudioRecordAudioTrackPage()
                AudioEnum.AudioCutting -> {}
            }
        }
    }
}