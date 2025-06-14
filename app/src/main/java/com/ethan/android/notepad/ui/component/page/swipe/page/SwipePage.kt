package com.ethan.android.notepad.ui.component.page.swipe.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.theme.Red
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.ui.component.page.swipe.view.SwipeableView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.ui.custom.view.TitleCardView
import com.ethan.android.notepad.utils.ToastType
import com.ethan.android.notepad.utils.showToast
import kotlinx.coroutines.delay

/**
 * 侧滑删除的三种实现方式
 *
 * 1.SwipeToDismissBox
 *
 * 2.SwipeToDismiss
 *
 * 3.swipeable修饰符
 */
@Composable
@Preview
fun SwipePage() {
    val isSelected = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView("侧滑组件", true)

        Column(modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
            TitleCardView("swipeable修饰符", modifier = Modifier.height(150.dp)) {
                SwipeableView(isSelected.value) { isSelected.value = !isSelected.value }
            }

            Spacer(modifier = Modifier.height(20.dp))
            TitleCardView("SwipeToDismissBox基本使用", modifier = Modifier.height(200.dp)) {
                SwipeToDeleteList()
            }

            Spacer(modifier = Modifier.height(20.dp))
            TitleCardView("SwipeToDismissBox动画效果", modifier = Modifier.height(200.dp)) {
                SwipeToDeleteListWithAnimation()
            }

            Spacer(modifier = Modifier.height(20.dp))
            TitleCardView("带撤销功能的滑动删除", modifier = Modifier.height(100.dp)) {}
        }
    }
}

@Composable
fun SwipeToDeleteList() {
    val context = LocalContext.current
    val items = remember { mutableStateListOf("Item 1", "Item 2", "Item 3") }

    LazyColumn {
        items(items, key = { item -> item }) { item ->
            SwipeToDeleteItem(item = item) {
                items.remove(item)
                "删除${item}成功".showToast(context, ToastType.SUCCESS)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SwipeToDeleteListWithAnimation() {
    val context = LocalContext.current
    val items = remember { mutableStateListOf("Item 1", "Item 2", "Item 3") }

    LazyColumn {
        items(items, key = { item -> item }) { item ->
            SmoothSwipeToDeleteItem(
                item = item,
                onDelete = {
                    items.remove(item)
                    "删除${item}成功".showToast(context, ToastType.SUCCESS)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SwipeToDeleteItem(item: String, onDelete: () -> Unit) {

    // 1. 定义滑动状态（支持 StartToEnd 和 EndToStart）
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                else -> false
            }
        },
        positionalThreshold = { distance -> distance * 0.5f } // 滑动 50% 才触发
    )

    // 2. 使用 SwipeToDismissBox
    SwipeToDismissBox(
        state = dismissState,
//        enableDismissFromStartToEnd = false,
        backgroundContent = {
            // 滑动时显示的背景（删除按钮）
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Red),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }, content = {
            // 正常显示的内容
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    )
}

@Composable
fun SmoothSwipeToDeleteItem(item: String, onDelete: () -> Unit) {
    // 1. 控制是否显示（用于动画）
    var isVisible by remember { mutableStateOf(true) }

    // 2. 滑动状态管理
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                isVisible = false // 触发消失动画
                true
            } else {
                false
            }
        }
    )

    // 3. 监听动画完成后再删除
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(300) // 等待动画播放完毕
            onDelete() // 真正删除数据
        }
    }

    // 4. 使用 AnimatedVisibility 控制动画
    AnimatedVisibility(
        visible = isVisible,
//        exit = fadeOut(animationSpec = tween(200)) +
//                shrinkVertically(
//                    animationSpec = spring(dampingRatio = 0.5f), //使用 spring 动画让回弹更自然
//                    shrinkTowards = Alignment.Top
//                )
        exit = fadeOut() + shrinkVertically(animationSpec = tween(300))
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                // 滑动时显示的背景（删除按钮）
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Red),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            },
            content = {
                // 正常显示的内容
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        )
    }
}