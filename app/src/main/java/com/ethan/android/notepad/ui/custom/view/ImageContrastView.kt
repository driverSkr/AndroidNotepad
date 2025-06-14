package com.ethan.android.notepad.ui.custom.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.ethan.android.notepad.extension.dpF
import com.ethan.android.notepad.utils.setAlpha

/**
 * 图片对比动画
 */
@Composable
fun ImageContrastView(before: ImageBitmap, after: ImageBitmap, startFrom: Float = 0F, endTo: Float = 1F, isPlayAnim: Boolean = false) {
    val alpha = remember { Animatable(0F) }
    val slidingAnim = remember { Animatable(startFrom) }
    val isVisible = remember { mutableStateOf(false) }
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val widthPixels = displayMetrics.widthPixels
    val heightPixels = displayMetrics.heightPixels

    LaunchedEffect(isPlayAnim) {
        if (isPlayAnim) {
            alpha.animateTo(targetValue = 1F, animationSpec = tween(durationMillis = 500, easing = LinearEasing))
            while (true) {
                slidingAnim.animateTo(endTo, tween(2500, easing = CubicBezierEasing(0.23F, 1.0F, 0.32F, 1.0F)))
                slidingAnim.animateTo(startFrom, tween(2500, easing = CubicBezierEasing(0.23F, 1.0F, 0.32F, 1.0F)))
            }
        } else {
            alpha.animateTo(0F, animationSpec = tween(durationMillis = 500, easing = LinearEasing))
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .clipToBounds()
        .onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            val size = coordinates.size
            isVisible.value = position.x + size.width > 0f && position.x < widthPixels.toFloat() && position.y + size.height > 0f && position.y < heightPixels.toFloat()

        }) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val scale = maxOf(canvasWidth / before.width.toFloat(), canvasHeight / before.height.toFloat())
        val scaledWidth = before.width * scale
        val scaledHeight = before.height * scale
        val offsetX = (canvasWidth - scaledWidth) / 2
        val offsetY = (canvasHeight - scaledHeight) / 2

        val currentPosition = startFrom + (endTo - startFrom) * slidingAnim.value

        drawImage(image = before, srcOffset = IntOffset.Zero, dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt()),
            dstSize = IntSize(scaledWidth.toInt(), scaledHeight.toInt()))

        if (isVisible.value) {
            drawImage(image = after, srcOffset = IntOffset((after.width * currentPosition).toInt(), 0),
                srcSize = IntSize((after.width * (1 - currentPosition)).toInt(), after.height),
                dstOffset = IntOffset((offsetX + scaledWidth * currentPosition).toInt(), offsetY.toInt()),
                dstSize = IntSize((scaledWidth * (1 - currentPosition)).toInt(), scaledHeight.toInt()), alpha = alpha.value)

            drawLine(brush = Brush.verticalGradient(listOf(Color.White.setAlpha(0.2F), Color.White, Color.White.setAlpha(0.0F))),
                start = Offset(x = offsetX + scaledWidth * currentPosition, y = offsetY),
                end = Offset(x = offsetX + scaledWidth * currentPosition, y = offsetY + scaledHeight), strokeWidth = 2.dpF,
                alpha = alpha.value)
        }
    }
}