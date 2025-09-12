package com.ethan.android.notepad.ui.media

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ethan.android.notepad.common.model.CardItem
import com.ethan.android.notepad.common.view.ListCardView
import com.ethan.android.notepad.common.view.StatusBarsView
import com.ethan.android.notepad.ui.media.audio.AudioRecordActivity
import com.ethan.android.notepad.ui.media.image.ImageActivity
import com.ethan.android.notepad.ui.media.picker.MediaPickerActivity
import com.ethan.android.notepad.ui.media.video.VideoActivity

/**
 * 多媒体组件：音视频播放、录制、裁剪
 */
@Composable
@Preview
fun MediaPage() {
    val context = LocalContext.current
    val items = listOf(
        CardItem("音频相关", true, isCompleted = false) { AudioRecordActivity.launch(context) },
        CardItem("图片相关", true, isCompleted = false) { ImageActivity.launch(context) },
        CardItem("视频相关", true, isCompleted = false) { VideoActivity.launch(context) },
        CardItem("本地媒体选择", true, isCompleted = false) { MediaPickerActivity.launch(context) }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "多媒体组件多媒体组件")

        ListCardView(items)
    }
}