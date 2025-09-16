package com.ethan.android.notepad.ui.technique.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ethan.android.notepad.common.model.CardItem
import com.ethan.android.notepad.common.view.ListCardView
import com.ethan.android.notepad.common.view.StatusBarsView

@Composable
fun DownloadPage() {
    val items = listOf(
        CardItem("普通下载", true, isCompleted = false),
        CardItem("下载带进度值", true, isCompleted = false) { },
        CardItem("下载队列", true, isCompleted = false) {  },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "资源下载")

        ListCardView(items = items)
    }
}