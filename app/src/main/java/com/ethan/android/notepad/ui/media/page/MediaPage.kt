package com.ethan.android.notepad.ui.media.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ethan.android.notepad.ui.custom.model.CardItem
import com.ethan.android.notepad.ui.custom.view.ListCardView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.ui.media.AudioRecordActivity

/**
 * 多媒体组件：音视频播放、录制、裁剪
 */
@Composable
@Preview
fun MediaPage() {
    val context = LocalContext.current
    val items = listOf(
        CardItem("音频录制、裁剪、播放", true, isCompleted = false) { AudioRecordActivity.launch(context) },
        CardItem("视频录制、裁剪、播放", true, isCompleted = false) {  }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView("多媒体组件多媒体组件")

        ListCardView(items)
    }
}