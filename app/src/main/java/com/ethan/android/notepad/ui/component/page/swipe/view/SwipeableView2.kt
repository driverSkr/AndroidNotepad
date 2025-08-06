package com.ethan.android.notepad.ui.component.page.swipe.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.RedFF5762
import kotlin.math.roundToInt

/** 滑动组件 */
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeableView2(
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    // 滑动状态控制
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val swipeThreshold = (-80).dp  // 负值表示向左滑动
    val swipeThresholdPx = with(LocalDensity.current) { swipeThreshold.toPx() }
    val anchors = mapOf(0f to 0, swipeThresholdPx to 1) // 定义滑动锚点

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .padding(horizontal = 8.dp)
        .swipeable(
            state = swipeableState,
            anchors = anchors,
            thresholds = { _, _ -> FractionalThreshold(0.5f) },
            orientation = Orientation.Horizontal
        )
    ) {
        // 删除区域
        Box(modifier = Modifier
            .size(80.dp)
            .background(color = RedFF5762)
            .align(Alignment.BottomEnd)
        ) {
            Image(painter = painterResource(R.drawable.svg_icon_delete_white), contentDescription = null, modifier = Modifier.align(Alignment.Center))
        }

        // 主内容
        VoiceItemView(
            isSelected = isSelected,
            onclick = onClick,
            modifier = Modifier.offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
        )
    }
}