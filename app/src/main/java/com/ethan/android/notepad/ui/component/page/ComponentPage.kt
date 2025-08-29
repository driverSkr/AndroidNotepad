package com.ethan.android.notepad.ui.component.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.ethan.android.notepad.ui.component.ComponentPreviewActivity
import com.ethan.android.notepad.ui.component.context.PageType
import com.ethan.android.notepad.ui.component.page.swipe.SwipeActivity
import com.ethan.android.notepad.common.model.CardItem
import com.ethan.android.notepad.common.view.ListCardView
import com.ethan.android.notepad.common.view.StatusBarsView
import com.ethan.android.notepad.utils.DialogHelper

/**
 * 基础组件：Android官方提供的组件的使用示例
 */
@Composable
@Preview
fun ComponentPage() {
    val context = LocalContext.current

    val items = listOf(
        CardItem("Text", false) {
            DialogHelper.componentBottomDialog(context as FragmentActivity, 0)
        },
        CardItem("Button", false) {
            DialogHelper.componentBottomDialog(context as FragmentActivity, 1)
        },
        CardItem("Image", false) {
            DialogHelper.componentBottomDialog(context as FragmentActivity, 2)
        },
        CardItem("ProgressIndicator", false) {
            DialogHelper.componentBottomDialog(context as FragmentActivity, 3)
        },
        CardItem("TextField", true) {
            ComponentPreviewActivity.launch(context, PageType.TextField)
        },
        CardItem("侧滑删除组件", true, isCompleted = false) {
            SwipeActivity.launch(context)
        },
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)) {
        StatusBarsView(title = "基础组件")
        ListCardView(items)
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun bottomDialog(showView: Int): SheetState {
//    val scope = rememberCoroutineScope()
//    //skipPartiallyExpanded = false 部分展开
//    val sheetState = rememberModalBottomSheetState(true, confirmValueChange = { true })
//
//    if (sheetState.isVisible) {
//        ModalBottomSheet(
//            sheetState = sheetState,
//            containerColor = White,
//            dragHandle = null,
//            contentWindowInsets = { WindowInsets(0,0,0,0) },
//            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
//            onDismissRequest = {
//                scope.launch(Dispatchers.Default) { sheetState.hide() }
//            }
//        ) {
//            AnimatedContent(showView, label = "") {
//                when (it) {
//                    0 -> TextPage()
//                    1 -> ButtonPage()
//                    2 -> ImagePage()
//                    3 -> ProgressIndicatorPage()
//                    4 -> TextFieldPage()
//                }
//            }
//        }
//    }
//
//    return sheetState
//}