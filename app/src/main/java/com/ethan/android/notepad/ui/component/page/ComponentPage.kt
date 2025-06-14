package com.ethan.android.notepad.ui.component.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.ui.component.page.swipe.SwipeActivity
import com.ethan.android.notepad.ui.custom.model.CardItem
import com.ethan.android.notepad.ui.custom.view.ListCardView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 基础组件：Android官方提供的组件的使用示例
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ComponentPage() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val showView = remember { mutableIntStateOf(0) }
    val dialog = bottomDialog(showView.intValue)

    val items = listOf(
        CardItem("Text", false) {
            showView.intValue = 0
            scope.launch {
                dialog.show()
            }
        },
        CardItem("Button", false) {
            showView.intValue = 1
            scope.launch {
                dialog.show()
            }
        },
        CardItem("Image", false) {
            showView.intValue = 2
            scope.launch {
                dialog.show()
            }
        },
        CardItem("ProgressIndicator", false) {
            showView.intValue = 3
            scope.launch {
                dialog.show()
            }
        },
        CardItem("TextField", false) {
            showView.intValue = 4
            scope.launch {
                dialog.show()
            }
        },
        CardItem("侧滑删除组件", true, isCompleted = false) {
            SwipeActivity.launch(context)
        },
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)) {
        StatusBarsView("基础组件")
        ListCardView(items)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomDialog(showView: Int): SheetState {
    val scope = rememberCoroutineScope()
    //skipPartiallyExpanded = false 部分展开
    val sheetState = rememberModalBottomSheetState(true, confirmValueChange = { true })

    if (sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = White,
            dragHandle = null,
            contentWindowInsets = { WindowInsets(0,0,0,0) },
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            onDismissRequest = {
                scope.launch(Dispatchers.Default) { sheetState.hide() }
            }
        ) {
            AnimatedContent(showView, label = "") {
                when (it) {
                    0 -> TextPage()
                    1 -> ButtonPage()
                    2 -> ImagePage()
                    3 -> ProgressIndicatorPage()
                    4 -> TextFieldPage()
                }
            }
        }
    }

    return sheetState
}