package com.ethan.android.notepad.ui.custom.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ethan.android.notepad.ui.custom.CustomPreviewActivity
import com.ethan.android.notepad.ui.custom.model.CardItem
import com.ethan.android.notepad.ui.custom.model.PageType
import com.ethan.android.notepad.ui.custom.view.ListCardView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.ui.dialog.view.rememberLoginDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 自定义组件: 自己编写或收集的完整的UI组件或框架
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CustomPage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dialog = rememberLoginDialog(context)
    val items = listOf(
        CardItem("底部登陆弹窗", false) {
            scope.launch(Dispatchers.Default) {
                dialog.show()
            }
        },
        CardItem("图片对比动画组件", true) {
            CustomPreviewActivity.launch(context, PageType.ImageComparePage)
        },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView("自定义组件")
        ListCardView(items)
    }
}