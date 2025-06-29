package com.ethan.android.notepad.ui.technique.page

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White60
import com.ethan.android.notepad.theme.parseColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

private const val TAG = "SwitchBtnColorWithBannerBgPage"

@Composable
@Preview
fun SwitchBtnColorWithBannerBgPage() {

    val colorList = listOf("#FFD700", "#FF0048", "#BC97FF")
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % colorList.size),
        pageCount = { Int.MAX_VALUE }
    )
    // 动态计算当前颜色和目标颜色（兼容左右滑动）
    val (currentColor, targetColor, progress) = calculateColorTransition(pagerState, colorList)

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
        ) {
            ConfigBannerView(pagerState, colorList)
        }

        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier
            .height(60.dp)
            .width(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(lerpColor(currentColor, targetColor, progress))
        ) {
            Text(
                text = "Button",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
    }
}

/**
 * 计算颜色过渡状态（兼容左右滑动）
 * @return Triple<当前颜色, 目标颜色, 过渡进度>
 */
@Composable
private fun calculateColorTransition(
    pagerState: PagerState,
    colorList: List<String>
): Triple<Color, Color, Float> {
    val currentPage = pagerState.currentPage
    val offsetFraction = pagerState.currentPageOffsetFraction

    return remember(pagerState.currentPage, pagerState.currentPageOffsetFraction) {
        val colorSize = colorList.size
        val currentIndex = currentPage % colorSize
        val currentColor = parseColor(colorList[currentIndex])

        // 判断滑动方向
        val isSwipingToNext = offsetFraction > 0 // 右滑（下一页）
        val targetIndex = if (isSwipingToNext) {
            (currentPage + 1) % colorSize
        } else {
            (currentPage - 1).mod(colorSize) // 处理负数情况
        }
        val targetColor = parseColor(colorList[targetIndex])

        // 计算过渡进度（0~1）
        val progress = offsetFraction.absoluteValue.coerceIn(0f, 1f)

        Triple(currentColor, targetColor, progress)
    }
}

/** 颜色插值计算（线性过渡） */
fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}

@Composable
fun ConfigBannerView( pagerState: PagerState, colorList: List<String>) {
    var isUserOption by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val anim = animateFloatAsState(if (pagerState.isScrollInProgress) 1F else 0F, label = "").value

    DisposableEffect(isUserOption) {
        val job = scope.launch(Dispatchers.Default) {
            while (!isUserOption) {
                delay(5000)
                if (!pagerState.isScrollInProgress) {
                    withContext(Dispatchers.Main) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        }
        onDispose {
            job.cancel()
        }
    }

    HorizontalPager(state = pagerState, userScrollEnabled = true, modifier = Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                when (event.type) {
                    PointerEventType.Press -> {
                        Log.d(TAG, "Banner 按下事件")
                        isUserOption = true
                    }

                    PointerEventType.Release -> {
                        Log.d(TAG, "Banner 抬起事件")
                        isUserOption = false
                    }
                }
            }
        }
    }) { index ->
        colorList.getOrNull(index % colorList.size)?.let { detail ->
            val verticalPadding = (12 * anim).dp
            val horizontalPadding = (6 * anim).dp
            Box(modifier = Modifier
                .padding(vertical = verticalPadding, horizontal = horizontalPadding)
                .clip(RoundedCornerShape(12.dp * anim))
                .fillMaxSize()
                .background(color = parseColor(detail))
            ) {
                BannerIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 30.dp),
                    pagerState,
                    colorList.size
                )
            }
        }
    }
}

@Composable
fun BannerIndicator(modifier: Modifier = Modifier, pagerState: PagerState, num: Int) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(num) { index ->
            val isSelected = index == pagerState.currentPage % num

            Box(modifier = Modifier
                .height(4.dp)
                .width(if (isSelected) 20.dp else 7.dp)
                .background(
                    color = if (isSelected) White else White60,
                    shape = RoundedCornerShape(34.dp)
                )
            )
        }
    }
}