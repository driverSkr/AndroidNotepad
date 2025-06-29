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

private const val TAG = "SwitchBtnColorWithBannerBgPage"

@Composable
@Preview
fun SwitchBtnColorWithBannerBgPage() {
    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
        ) {
            ConfigBannerView()
        }

        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier
            .height(60.dp)
            .width(150.dp)
            .background(color = Black, shape = RoundedCornerShape(12.dp))
        )
    }
}

@Composable
fun ConfigBannerView() {
    val colorList = listOf("#FFD700", "#FF0048", "#BC97FF")

    //虚拟无限列表,无限轮播效果
    val initialPage = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % colorList.size)
    val pagerState = rememberPagerState(initialPage) { Int.MAX_VALUE }
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