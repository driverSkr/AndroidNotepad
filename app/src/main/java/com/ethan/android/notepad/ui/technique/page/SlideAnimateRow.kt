package com.ethan.android.notepad.ui.technique.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.common.utils.antiShakeClick
import com.ethan.android.notepad.theme.brushColor
import com.ethan.android.notepad.theme.brushColorReverse
import com.ethan.android.notepad.theme.colorList
import com.ethan.android.notepad.theme.parseColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SlideAnimateRow() {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    // 添加滑动监听
    val isFirstVisible by remember {
        derivedStateOf {
            // 检查第一个item是否完全可见
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
            // 第一个item是索引0且没有偏移量（完全可见）
            firstVisibleIndex == 0 && firstVisibleItemScrollOffset == 0
        }
    }
    val isLastVisible by remember {
        derivedStateOf {
            // 检查最后一个item是否完全可见
            val layoutInfo = listState.layoutInfo
            val lastItemIndex = colorList.size - 1
            // 检查最后一个item是否在可见范围内
            val visibleItems = layoutInfo.visibleItemsInfo
            val lastVisibleItem = visibleItems.lastOrNull()
            // 如果最后一个可见项是列表的最后一项，并且完全可见
            lastVisibleItem?.index == lastItemIndex && (lastVisibleItem.offset + lastVisibleItem.size) <= layoutInfo.viewportEndOffset
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().height(80.dp).align(Alignment.Center)) {
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(colorList.size) { index ->
                    Box(modifier = Modifier
                        .width(96.dp)
                        .height(80.dp)
                        .background(color = parseColor(colorList[index]), shape = RoundedCornerShape(14.dp))
                    )
                }
            }

            AnimatedVisibility(
                visible = isLastVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .width(32.dp)
                        .background(brush = Brush.horizontalGradient(colorStops = if (isRtl) brushColorReverse else brushColor))
                        .antiShakeClick {
                            scope.launch(Dispatchers.Main) {
                                listState.animateScrollToItem(0)
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.svg_icon_back_with_bg),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            AnimatedVisibility(
                visible = isFirstVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500)),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .width(32.dp)
                        .background(brush = Brush.horizontalGradient(colorStops = if (isRtl) brushColor else brushColorReverse))
                        .antiShakeClick {
                            scope.launch(Dispatchers.Main) {
                                listState.animateScrollToItem(colorList.size - 1)
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.svg_icon_next_with_bg),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}