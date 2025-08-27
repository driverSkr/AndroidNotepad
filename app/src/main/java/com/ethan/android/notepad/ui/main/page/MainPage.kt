package com.ethan.android.notepad.ui.main.page

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ethan.android.notepad.extension.findBaseActivityVBind
import com.ethan.android.notepad.ui.component.ComponentActivity
import com.ethan.android.notepad.ui.composite.CompositeActivity
import com.ethan.android.notepad.ui.custom.CustomActivity
import com.ethan.android.notepad.ui.custom.model.CardItem
import com.ethan.android.notepad.ui.custom.view.ListCardView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.ui.dialog.DialogActivity
import com.ethan.android.notepad.ui.material.MaterialActivity
import com.ethan.android.notepad.ui.media.MediaActivity
import com.ethan.android.notepad.ui.room.RoomActivity
import com.ethan.android.notepad.ui.technique.TechniqueActivity
import com.ethan.android.notepad.ui.test.TestActivity
import com.ethan.android.notepad.utils.ShowToast.showToast

/**
 * 首页
 */
@Composable
@Preview
fun MainPage() {
    val context = LocalContext.current
    val items = listOf(
        CardItem("基础组件", true) { ComponentActivity.launch(context) },
        CardItem("组合组件", true) { CompositeActivity.launch(context) },
        CardItem("自定义组件", true) { CustomActivity.launch(context) },
        CardItem("弹窗组件", true) { DialogActivity.launch(context) },
        CardItem("多媒体组件", true) { MediaActivity.launch(context) },
        CardItem("Room数据库", true) { RoomActivity.launch(context) },
        CardItem("技术、技巧、知识", true) { TechniqueActivity.launch(context) },
        CardItem("素材组件库", true) { MaterialActivity.launch(context) },
        CardItem("Test", true) { TestActivity.launch(context) },
    )

    var lastTime by remember { mutableLongStateOf(0L) }
    BackHandler {
        val now = System.currentTimeMillis()
        if (now - lastTime > 2000) {
            showToast("再按一次退出程序")
            lastTime = now
            return@BackHandler
        }
        context.findBaseActivityVBind()?.finish()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "主页", canBack = false)
        ListCardView(items)
    }
}