package com.ethan.android.notepad.ui.media.image.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ethan.android.notepad.common.model.CardItem
import com.ethan.android.notepad.common.view.ListCardView
import com.ethan.android.notepad.common.view.StatusBarsView
import com.ethan.android.notepad.ui.media.image.ImagePreviewActivity
import com.ethan.android.notepad.ui.media.image.context.ImagePageType

@Composable
fun ImagePage() {
    val context = LocalContext.current

    val items = listOf(
        CardItem("图片加水印", true, isCompleted = false) {
            ImagePreviewActivity.launch(context, ImagePageType.Watermark)
        },
        CardItem("图片查看组件", true, isCompleted = false) {
            ImagePreviewActivity.launch(context, ImagePageType.ImageZoom)
        },
        CardItem("图片对比组件", true, isCompleted = false) {
            ImagePreviewActivity.launch(context, ImagePageType.ImageCompare)
        },
        CardItem("图片加载组件", true, isCompleted = false) {
            ImagePreviewActivity.launch(context, ImagePageType.ImageLoad)
        },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "图片相关")

        ListCardView(items)
    }
}