package com.ethan.android.notepad.ui.technique.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ethan.android.notepad.common.model.CardItem
import com.ethan.android.notepad.common.view.ListCardView
import com.ethan.android.notepad.common.view.StatusBarsView
import com.ethan.android.notepad.ui.technique.TechniquePreviewActivity
import com.ethan.android.notepad.ui.technique.model.PageType

/**
 * Modifier.onGloballyPositioned {  }
 * Modifier.onPlaced {  }
 */
@Composable
@Preview
fun ModifierPage() {
    val context = LocalContext.current
    val items = listOf(
        CardItem("Pair和Triple的使用", true, isCompleted = false),
        CardItem("aspectRatio设置宽高比", true, isCompleted = false),
        CardItem("imePadding自动增加底部内边距", true) { TechniquePreviewActivity.launch(context, PageType.ImePadding) },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "Modifier实用方法")

        ListCardView(items = items)
    }
}