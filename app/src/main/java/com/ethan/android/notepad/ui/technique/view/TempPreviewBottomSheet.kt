package com.ethan.android.notepad.ui.technique.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.theme.color1
import com.ethan.android.notepad.theme.color2
import com.ethan.android.notepad.theme.color3
import com.ethan.android.notepad.theme.color4
import com.ethan.android.notepad.theme.color5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tempPreviewBottomSheet() : SheetState {
    val scope = rememberCoroutineScope()
    //跳过"半展开"状态，直接进入完全展开状态
    //允许弹窗在任何状态间自由转换，不做限制
    val sheetState = rememberModalBottomSheetState(true, confirmValueChange = { true })

    if (sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = Transparent,
            dragHandle = null,  //移除顶部默认的拖动把手(横条)
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
            shape = RectangleShape,
            tonalElevation = 32.dp,
            onDismissRequest = {
                scope.launch(Dispatchers.Main) { sheetState.hide() }
            }
        ) {
            BottomSheetView(
                sheetState = sheetState,
                onFullExpand = {
                    scope.launch {
                        // 可以在这里添加全屏后的额外逻辑
                    }
                }
            )
        }
    }

    return sheetState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetView(
    sheetState: SheetState,
    onFullExpand: () -> Unit
) {
    val bgColor = listOf(color1, color2, color3, color4, color5)
    val pagerState = rememberPagerState(0) { 5 }
    val canScroll = remember { mutableStateOf(true) }

    // 动态调整的参数
    var contentPadding by remember { mutableFloatStateOf(24f) }
    var heightFraction by remember { mutableFloatStateOf(0.95f) }

    // 使用动画使过渡更平滑
    val animatedHeightFraction by animateFloatAsState(
        targetValue = heightFraction,
        animationSpec = tween(durationMillis = 200)
    )

    val animatedContentPadding by animateFloatAsState(
        targetValue = contentPadding,
        animationSpec = tween(durationMillis = 200)
    )

    // 计算安全的圆角尺寸（确保不为负）
    val cornerRadius = remember(animatedHeightFraction) {
        (12 * (1 - (animatedHeightFraction - 0.95f) * 20)).coerceAtLeast(0f)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(animatedHeightFraction)
    ) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 2,
            contentPadding = PaddingValues(horizontal = animatedContentPadding.dp),
            pageSpacing = 3.dp,
            userScrollEnabled = canScroll.value
        ) { page ->
            Box(modifier = Modifier
                .fillMaxSize()
                .background(
                    color = bgColor[page],
                    shape = RoundedCornerShape(
                        topStart = cornerRadius.dp,
                        topEnd = cornerRadius.dp
                    )
                )
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (sheetState.isVisible) {
                            // 手势方向处理：
                            // 上滑(dragAmount为负) -> 高度增加，padding减小
                            // 下滑(dragAmount为正) -> 高度减小，padding增加
                            val adjustmentRatio = dragAmount / size.height

                            // 高度变化：上滑增加，下滑减小
                            heightFraction = (heightFraction - adjustmentRatio).coerceIn(0.95f, 1f)
                            // padding变化：上滑减小，下滑增加
                            // 24f是最大padding，所以变化比例是 (1 - heightFraction) * 24 * 5
                            contentPadding = 24f * (1 - (heightFraction - 0.95f) * 20)
                            // 边界检查
                            contentPadding = contentPadding.coerceIn(0f, 24f)

                            // 如果达到全屏，触发回调
                            if (heightFraction >= 0.99f) {
                                onFullExpand()
                            }

                            if (heightFraction == 0.95f) {
                                canScroll.value = true
                            } else {
                                canScroll.value = false
                            }
                        }
                    }
                }
            ) {
                TempPreviewPagerView()
            }
        }
    }
}

@Composable
fun TempPreviewPagerView() {

}