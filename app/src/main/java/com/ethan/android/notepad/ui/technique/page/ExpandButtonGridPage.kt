package com.ethan.android.notepad.ui.technique.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.common.utils.antiShakeClick
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.color9
import kotlin.math.ceil

@Composable
fun ExpandButtonGridPage() {
    Column(modifier = Modifier.fillMaxSize().background(color = color9)) {
        ExpandableButtonGrid()

        Spacer(modifier = Modifier.height(20.dp))

        ExpandableButtonGrid(
            items = (1..20).map { "功能 $it" },
            columns = 4,
            maxRowsWhenCollapsed = 1
        )

        Spacer(modifier = Modifier.height(20.dp))

        ExpandableGrid()
    }
}

@Composable
fun ExpandableGrid() {
    var isExpanded by remember { mutableStateOf(false) }
    val columns = 5
    // 模拟按钮数据
    val items = listOf(
        Pair("增强", R.drawable.svg_icon_func_speed_ve),
        Pair("图生视频", R.drawable.svg_icon_func_image_2_video),
        Pair("文生视频", R.drawable.svg_icon_func_text_2_video),
        Pair("美妆", R.drawable.svg_icon_func_beauty),
        Pair("老照片修复", R.drawable.svg_icon_func_old_photo_restore),
        Pair("AI消除", R.drawable.svg_icon_func_ai_removal),
        Pair("数字人", R.drawable.svg_icon_func_digital_human),
        Pair("AI抠像", R.drawable.svg_icon_func_ai_cutout),
    )

    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp).animateContentSize()) {
        // 第一行：4个项 + 1个按钮
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 显示前4项
            val firstRowItems = if (isExpanded) items.take(4) else items.take(4)
            firstRowItems.forEachIndexed { index, func ->
                Column(modifier = Modifier.height(64.dp).weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Image(painter = painterResource(func.second), contentDescription = null, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(func.first, fontSize = 10.sp, color = White, textAlign = TextAlign.Center, fontWeight = FontWeight.W400)
                }
            }
            // 第5个位置：按钮
            Column(modifier = Modifier.height(64.dp).weight(1f).antiShakeClick{ isExpanded = !isExpanded }, horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(4.dp))
                Image(painter = painterResource(if (isExpanded) R.drawable.svg_icon_collapse else R.drawable.svg_icon_expand), contentDescription = null, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(if (isExpanded) "收起" else "展开", fontSize = 10.sp, color = White, textAlign = TextAlign.Center, fontWeight = FontWeight.W400)
            }
        }

        // 如果展开，显示剩余项目
        if (isExpanded) {
            // 每行显示5个项目
            val remainingItems = items.drop(4)
            val rows = ceil(remainingItems.size / columns.toFloat()).toInt()

            repeat(rows) { rowIndex ->
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val startIndex = rowIndex * columns
                    val endIndex = minOf(startIndex + columns, remainingItems.size)

                    // 当前行的项目
                    for (i in startIndex until endIndex) {
                        Column(modifier = Modifier.height(64.dp).weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Image(painter = painterResource(remainingItems[i].second), contentDescription = null, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(remainingItems[i].first, fontSize = 10.sp, color = White, textAlign = TextAlign.Center, fontWeight = FontWeight.W400)
                        }
                    }

                    // 如果当前行不满5个，用空白填充
                    repeat(columns - (endIndex - startIndex)) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableButtonGrid() {
    var isExpanded by remember { mutableStateOf(false) }

    // 模拟按钮数据
    val buttonItems = listOf(
        "按钮 1", "按钮 2", "按钮 3", "按钮 4",
        "按钮 5", "按钮 6", "按钮 7", "按钮 8",
        "按钮 9", "按钮 10", "按钮 11", "按钮 12"
    )

    // 计算第一排需要显示的按钮数量（4列）
    val firstRowCount = 4
    val visibleItems = if (isExpanded) buttonItems else buttonItems.take(firstRowCount)

    Column(modifier = Modifier.statusBarsPadding().fillMaxWidth().padding(16.dp)) {
        // 按钮网格区域
        AnimatedVisibility(
            visible = true,
            enter = expandVertically(
                animationSpec = tween(durationMillis = 300),
                expandFrom = Alignment.Top
            ) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 300),
                shrinkTowards = Alignment.Top
            ) + fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibleItems.size) { index ->
                    GridButton(text = visibleItems[index]) { }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExpandButton(isExpanded = isExpanded) { isExpanded = !isExpanded }
    }
}

/**
 * 进阶版：支持自定义列数和更好的动画效果
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandableButtonGrid(
    items: List<String>,
    columns: Int = 4,
    maxRowsWhenCollapsed: Int = 1
) {
    var isExpanded by remember { mutableStateOf(false) }

    // 计算显示的项目数量
    val collapsedItemCount = minOf(items.size, columns * maxRowsWhenCollapsed)
    val visibleItems = if (isExpanded) items else items.take(collapsedItemCount)

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                fadeIn(animationSpec = tween(300, 150)) with fadeOut(animationSpec = tween(300)) + slideOutVertically(animationSpec = tween(300), targetOffsetY = { -it })
            }
        ) { expanded ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visibleItems.size) { index ->
                    AnimateGridButton(text = visibleItems[index]) { }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 只在有更多内容可展开时显示按钮
        if (items.size > collapsedItemCount) {
            ExpandToggleButton(
                isExpanded = isExpanded,
                onClick = { isExpanded = !isExpanded },
                expandedText = "收起（${items.size}个）",
                collapsedText = "展开更多（${items.size - collapsedItemCount}个）"
            )
        }
    }
}

@Composable
fun GridButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(text = text, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 1)
    }
}

@Composable
fun AnimateGridButton(text: String, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            } else MaterialTheme.colorScheme.primary,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        ),
        interactionSource = remember { MutableInteractionSource() }.also { source ->
            LaunchedEffect(source) {
                source.interactions.collect { interaction ->
                    when(interaction) {
                        is PressInteraction.Press -> isPressed = true
                        is PressInteraction.Release -> isPressed = false
                        is PressInteraction.Cancel -> isPressed = false
                    }
                }
            }
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = text, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 2, lineHeight = 16.sp)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandToggleButton(
    isExpanded: Boolean,
    onClick: () -> Unit,
    expandedText: String = "收起",
    collapsedText: String = "展开更多"
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) with fadeOut(animationSpec = tween(200))
            }
        ) { expanded ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = if (expanded) expandedText else collapsedText)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ExpandButton(isExpanded: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        val text = if (isExpanded) "收起" else "展开更多"
        val icon = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}