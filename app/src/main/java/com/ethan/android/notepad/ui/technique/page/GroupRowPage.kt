package com.ethan.android.notepad.ui.technique.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.RedFF5F2D
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White12
import com.ethan.android.notepad.theme.White32

@Composable
@Preview
fun GroupRowPage() {
    val itemList = listOf(
        GridItem(1, Size.LARGE),
        GridItem(2, Size.SMALL),
        GridItem(3, Size.SMALL),
        GridItem(4, Size.SMALL),
        GridItem(5, Size.LARGE),
        GridItem(6, Size.LARGE),
        GridItem(7, Size.SMALL),
        GridItem(8, Size.SMALL),
        GridItem(9, Size.LARGE),
        GridItem(10, Size.SMALL),
        GridItem(11, Size.SMALL),
        GridItem(12, Size.SMALL),
        GridItem(13, Size.SMALL),
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(top = 10.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(232.dp)) {
            GroupRowView(itemList)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            FuncGroupRowView()
        }
    }
}

/**
 * 分组列表
 */
@Composable
fun GroupRowView(items: List<GridItem>) {
    // 先将原始列表转换为布局需要的分组
    val layoutGroups = remember(items) {
        val groups = mutableListOf<LayoutGroup>()
        var i = 0
        while (i < items.size) {
            when (items[i].size) {
                Size.LARGE -> {
                    groups.add(LayoutGroup.LargeItem(items[i]))
                    i++
                }
                Size.SMALL -> {
                    // 收集连续的SMALL项(最多2个为一组)
                    val smallItems = mutableListOf(items[i])
                    i++
                    if (i < items.size && items[i].size == Size.SMALL) {
                        smallItems.add(items[i])
                        i++
                    }
                    groups.add(LayoutGroup.SmallItems(smallItems))
                }
            }
        }
        groups
    }

    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(layoutGroups) { group ->
            when (group) {
                is LayoutGroup.LargeItem -> {
                    LargeItem(group.item)
                }
                is LayoutGroup.SmallItems -> {
                    if (group.items.size == 1) {
                        SmallItem(group.items[0])
                    } else {
                        Column {
                            group.items.forEachIndexed { index, item ->
                                SmallItem(item)
                                if (index + 1 < group.items.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmallItem(item: GridItem) {
    Box(
        modifier = Modifier
            .width(72.dp)
            .height(110.dp)
            .border(width = 1.dp, color = White32, shape = RoundedCornerShape(10.dp))
            .background(Color.Gray, shape = RoundedCornerShape(10.dp))
    ) {
        Text("项${item.id}\n1×1", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun LargeItem(item: GridItem) {
    Box(
        modifier = Modifier
            .width(156.dp)
            .height(232.dp)
            .border(width = 1.dp, color = White12, shape = RoundedCornerShape(12.dp))
            .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
    ) {
        Text("项${item.id}\n2×2", modifier = Modifier.align(Alignment.Center))
    }
}




///////////////////
@Composable
fun FuncGroupRowView() {
    val items = listOf(
        GridItem(1, Size.LARGE),
        GridItem(2, Size.SMALL),
        GridItem(3, Size.SMALL),
        GridItem(4, Size.SMALL),
        GridItem(5, Size.SMALL),
        GridItem(6, Size.LARGE),
        GridItem(7, Size.SMALL),
        GridItem(8, Size.SMALL),
        GridItem(9, Size.LARGE),
        GridItem(10, Size.SMALL),
        GridItem(11, Size.SMALL),
        GridItem(12, Size.SMALL),
    )

    // 先将原始列表转换为布局需要的分组
    val layoutGroups = remember(items) {
        val groups = mutableListOf<LayoutGroup>()
        var i = 0
        while (i < items.size) {
            when (items[i].size) {
                Size.LARGE -> {
                    groups.add(LayoutGroup.LargeItem(items[i]))
                    i++
                }
                Size.SMALL -> {
                    // 收集连续的SMALL项(最多2个为一组)
                    val smallItems = mutableListOf(items[i])
                    i++
                    if (i < items.size && items[i].size == Size.SMALL) {
                        smallItems.add(items[i])
                        i++
                    }
                    groups.add(LayoutGroup.SmallItems(smallItems))
                }
            }
        }
        groups
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("图生视频", fontSize = 16.sp, fontWeight = FontWeight.W700, color = Black)
            Spacer(modifier = Modifier.weight(1f))
            Text("更多", fontSize = 14.sp, fontWeight = FontWeight.W400, color = Black)
            Spacer(modifier = Modifier.width(2.dp))
            Image(painter = painterResource(R.drawable.svg_icon_next_black), contentDescription = null)
        }

        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth().height(232.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(layoutGroups) { group ->
                when (group) {
                    is LayoutGroup.LargeItem -> {
                        GroupLargeItemView(group.item)
                    }
                    is LayoutGroup.SmallItems -> {
                        if (group.items.size == 1) {
                            GroupSmallItemView(group.items[0])
                        } else {
                            Column {
                                group.items.forEachIndexed { index, item ->
                                    GroupSmallItemView(item)
                                    if (index + 1 < group.items.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupLargeItemView(item: GridItem) {
    Box(
        modifier = Modifier
            .width(156.dp)
            .height(232.dp)
            .border(width = 1.dp, color = White12, shape = RoundedCornerShape(12.dp))
            .background(RedFF5F2D, shape = RoundedCornerShape(12.dp))
    )
}

@Composable
private fun GroupSmallItemView(item: GridItem) {
    Box(
        modifier = Modifier
            .width(72.dp)
            .height(110.dp)
            .border(width = 1.dp, color = White32, shape = RoundedCornerShape(10.dp))
            .background(RedFF5F2D, shape = RoundedCornerShape(10.dp))
    )
}
//////////////////

// 定义布局分组类型
sealed class LayoutGroup {
    data class LargeItem(val item: GridItem) : LayoutGroup()
    data class SmallItems(val items: List<GridItem>) : LayoutGroup()
}

enum class Size { SMALL, LARGE }
data class GridItem(val id: Int, val size: Size)