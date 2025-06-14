package com.ethan.android.notepad.ui.media.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ethan.android.notepad.ui.custom.model.CardItem
import com.ethan.android.notepad.ui.custom.view.ListCardView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.ui.media.view.AudioRecordAudioTrackView
import com.ethan.android.notepad.ui.media.view.MediaRecorderExoPlayerView
import com.ethan.android.notepad.ui.media.view.RecordView

@Composable
@Preview
fun AudioRecordPage() {

    var selectPage by remember { mutableStateOf(AudioRecordPageEnum.MediaRecorderMediaPlayer) }
    val items = listOf(
        CardItem("MediaRecorder 录音 + MediaPlayer  播放", true) { selectPage =
            AudioRecordPageEnum.MediaRecorderMediaPlayer
        },
        CardItem("MediaRecorder 录音 + ExoPlayer  播放", true) { selectPage =
            AudioRecordPageEnum.MediaRecorderExoPlayer
        },
        CardItem("AudioRecord 录音 + AudioTrack  播放", true) { selectPage =
            AudioRecordPageEnum.AudioRecordAudioTrack
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView("录音")
        ListCardView(items)

        AnimatedContent(selectPage, label = "") {
            when(it) {
                AudioRecordPageEnum.MediaRecorderMediaPlayer -> RecordView()
                AudioRecordPageEnum.MediaRecorderExoPlayer -> MediaRecorderExoPlayerView()
                AudioRecordPageEnum.AudioRecordAudioTrack -> AudioRecordAudioTrackView()
            }
        }
    }
}

enum class AudioRecordPageEnum {
    /**
     * MediaRecorder 录音
     * MediaPlayer  播放
     */
    MediaRecorderMediaPlayer,

    /**
     * MediaRecorder 录音
     * ExoPlayer  播放
     */
    MediaRecorderExoPlayer,

    /**
     * AudioRecord 录音（采集PCM数据，支持暂停时缓存数据到内存/文件
     * AudioTrack 播放（直接播放PCM数据）
     */
    AudioRecordAudioTrack,
}