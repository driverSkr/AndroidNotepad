package com.ethan.android.notepad.ui.media.image.page

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.common.extension.findBaseActivityVBind
import com.ethan.android.notepad.common.model.CardItem
import com.ethan.android.notepad.common.view.ListCardView
import com.ethan.android.notepad.common.view.StatusBarsView
import com.ethan.android.notepad.common.view.TitleCardView
import com.ethan.android.notepad.ui.media.image.PhotoViewActivity

@Composable
fun ImageZoomPage() {
    val context = LocalContext.current
    val selectPageIndex = remember { mutableIntStateOf(0) }

    val items = listOf(
        CardItem("单图缩进组件", false, isCompleted = false) {
            selectPageIndex.intValue = 0
        },
        CardItem("双图缩进组件", false, isCompleted = false) {
            selectPageIndex.intValue = 1
        },
        CardItem("图片查看组件(原生版)", false, isCompleted = false) {
            context.findBaseActivityVBind()?.let {
                it.startActivity(Intent(it, PhotoViewActivity::class.java))
            }
        },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "图片相关")

        ListCardView(items)

        TitleCardView(isShowTitle = false, modifier = Modifier.padding(vertical = 20.dp), contentModifier = Modifier.weight(1f)) {
            AnimatedContent(selectPageIndex.intValue) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (it) {
                        0 -> {}
                        1 -> {}
                    }
                }
            }
        }
    }
}