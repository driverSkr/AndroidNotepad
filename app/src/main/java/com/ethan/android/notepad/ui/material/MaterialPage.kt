package com.ethan.android.notepad.ui.material

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ethan.android.notepad.ui.custom.model.CardItem
import com.ethan.android.notepad.ui.custom.view.ListCardView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView

@Composable
fun MaterialPage() {
    val context = LocalContext.current
    val items = listOf(
        CardItem("弹窗", false) {
            MaterialPreviewActivity.launch(context, PageType.Dialog)
        },
        CardItem("图片对比组件", false) {
            MaterialPreviewActivity.launch(context, PageType.ImageCompare)
        },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "素材组件库")
        ListCardView(items)
    }
}