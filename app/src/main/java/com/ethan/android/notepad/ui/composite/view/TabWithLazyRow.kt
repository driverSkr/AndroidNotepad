package com.ethan.android.notepad.ui.composite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.ethan.android.notepad.ui.custom.view.ScrollableTabRow
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Purple291E3D
import com.ethan.android.notepad.theme.PurpleBC97FF
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.ui.composite.model.removeDataSource
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
@Preview
fun TabWithLazyRow() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedMaterialIndex by remember { mutableIntStateOf(-2) }
    val allDetails by remember { mutableStateOf(removeDataSource.category.flatMap { it.detail }) }

    val itemWidths = remember { mutableStateListOf<Int>() }
    val screenWidthPx = with(LocalDensity.current) { ScreenUtils.getScreenWidth() - SizeUtils.dp2px(16f) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // 监听 LazyRow 的滚动位置，并根据滚动位置更新选中的 Tab
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { firstVisibleItemIndex ->
                if (firstVisibleItemIndex >= 1) {
                    val visibleItem = allDetails[firstVisibleItemIndex - 1]
                    val newTabIndex = removeDataSource.category.indexOfFirst { it.detail.contains(visibleItem) }
                    if (newTabIndex != -1 && newTabIndex != selectedTabIndex) {
                        selectedTabIndex = newTabIndex
                    }
                }
            }
    }

    LaunchedEffect(selectedMaterialIndex) {
        if (selectedMaterialIndex >= 0) {
            val itemIndex = selectedMaterialIndex + 1
            scope.launch {
                // 计算当前item之前所有item的宽度
                val previousWidths = itemWidths
                    .take(itemIndex)
                    .sum()
                // 计算当前item的宽度
                val currentWidth = itemWidths.getOrNull(itemIndex) ?: SizeUtils.dp2px(64f)
                // 计算目标偏移量
                val targetOffset = previousWidths + currentWidth / 2 - (screenWidthPx / 2)
                listState.animateScrollToItem(itemIndex, targetOffset)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().height(500.dp).statusBarsPadding()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp, // 设置边缘内边距
            containerColor = Transparent, // 设置透明背景以便自定义每个Tab的背景
            indicator = {}, // 去掉底部指示器
            divider = {}, // 去掉底部分隔线
        ) {
            removeDataSource.category.forEachIndexed { index, data ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index

                        // 找到当前标签页的第一个子项
                        val startIndex = allDetails.indexOfFirst { data.detail.contains(it) }
                        if (startIndex != -1) {
                            scope.launch {
                                val scrollToIndex = if (startIndex == 0) 0 else startIndex + 1
                                listState.scrollToItem(scrollToIndex)
                            }
                        }
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 8.dp)
                        .background(color = if (selectedTabIndex == index) Purple291E3D else Transparent, shape = RoundedCornerShape(20.dp))
                ) {
                    Text(
                        text = data.title,
                        color = if (selectedTabIndex == index) PurpleBC97FF else White,
                        fontSize = 12.sp,
                        style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                VideoSelectView(selectedMaterialIndex == -1) {
                    selectedMaterialIndex = -1
                }
            }
            itemsIndexed(allDetails) { index, detail ->
                val isLoad = remember { mutableStateOf(false) }
                RemoveItem(index == selectedMaterialIndex, isLoad, detail, {
                    selectedMaterialIndex = index
                })
            }
        }

    }
}