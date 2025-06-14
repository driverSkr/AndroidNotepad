package com.ethan.android.notepad.ui.component.page.swipe.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.wear.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ethan.android.notepad.R
import com.ethan.android.notepad.utils.ToastType
import com.ethan.android.notepad.utils.antiShakeClick
import com.ethan.android.notepad.utils.showToast
import kotlin.math.roundToInt

/** 滑动组件 */
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeableView(
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    // 滑动状态控制
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val swipeThreshold = (-79).dp  // 负值表示向左滑动
    val swipeThresholdPx = with(LocalDensity.current) { swipeThreshold.toPx() }
    val anchors = mapOf(0f to 0, swipeThresholdPx to 1) // 定义滑动锚点
    var isShowDelete by remember { mutableStateOf(false) }

    // 滑动结束时检查是否达到删除阈值
    LaunchedEffect(swipeableState.currentValue) {
        if (swipeableState.currentValue == 1) {
            isShowDelete = true
        }
        if (swipeableState.currentValue == 0) {
            isShowDelete = false
        }
    }

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
        AnimatedVisibility(isShowDelete, modifier = Modifier.padding(end = 6.dp).align(Alignment.CenterEnd)) {
            Row(modifier = Modifier.wrapContentSize()) {
                Image(painter = painterResource(R.drawable.svg_icon_voice_name_edit_round), contentDescription = null, modifier = Modifier.antiShakeClick { "你点击了编辑按钮".showToast(context, ToastType.SUCCESS) })
                Spacer(modifier = Modifier.width(6.dp))
                Image(painter = painterResource(R.drawable.svg_icon_delete_red), contentDescription = null, modifier = Modifier.antiShakeClick { "你点击了删除按钮".showToast(context, ToastType.SUCCESS) })
            }
        }

        // 主内容
        VoiceItemView(
            isSelected = isSelected,
            onclick = onClick,
            modifier = Modifier.offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
        )
    }
}