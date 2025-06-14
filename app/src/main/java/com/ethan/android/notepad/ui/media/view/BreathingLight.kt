package com.ethan.android.notepad.ui.media.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.theme.RedFF0048
import kotlinx.coroutines.launch

//红点呼吸灯
@Composable
fun BreathingLight() {

    val animation = remember { Animatable(0.8f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            animation.animateTo(
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.8f at 0 using LinearEasing
                        1.2f at 600 using FastOutSlowInEasing
                        0.8f at 1200 using LinearEasing
                    }
                )
            )
        }
    }

    Box(modifier = Modifier
        .size(6.dp)
        .graphicsLayer {
            scaleX = animation.value
            scaleY = animation.value
            alpha = 0.5f + (animation.value - 0.8f) * 1.25f // 同步计算透明度
        }
        .background(
            color = RedFF0048,
            shape = RoundedCornerShape(999.dp)
        )
    )
}