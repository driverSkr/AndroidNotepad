package com.ethan.android.notepad.ui.composite.page

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.ui.custom.view.ListCardView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.theme.Black_24252C
import com.ethan.android.notepad.ui.composite.view.TabWithHorizontalPager
import com.ethan.android.notepad.ui.composite.view.TabWithLazyRow
import com.ethan.android.notepad.ui.custom.model.CardItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 组合组件：对于多个组件的联合使用的示例
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CompositePage() {
    val scope = rememberCoroutineScope()
    val showView = remember { mutableIntStateOf(0) }
    val dialog = bottomDialog(showView.intValue)

    val items = listOf(
        CardItem("ScrollableTabRow和HorizontalPager联动", false) {
            showView.intValue = 0
            scope.launch {
                dialog.show()
            }
        },
        CardItem("ScrollableTabRow和LazyRow联动", false) {
            showView.intValue = 1
            scope.launch {
                dialog.show()
            }
        },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView("组合组件", true)
        ListCardView(items = items)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomDialog(showView: Int): SheetState {
    val scope = rememberCoroutineScope()
    //skipPartiallyExpanded = false 部分展开
    val sheetState = rememberModalBottomSheetState(false, confirmValueChange = { true })

    if (sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = Black_24252C,
            dragHandle = null,
            contentWindowInsets = { WindowInsets(0,0,0,0) },
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            onDismissRequest = {
                scope.launch(Dispatchers.Default) { sheetState.hide() }
            }
        ) {
            AnimatedContent(showView, label = "") {
                when (it) {
                    0 -> {
                        TabWithHorizontalPager(sheetState)
                    }
                    1 -> {
                        TabWithLazyRow()
                    }
                }
            }
        }
    }

    return sheetState
}