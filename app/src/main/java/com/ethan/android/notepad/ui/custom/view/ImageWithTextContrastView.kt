package com.ethan.android.notepad.ui.custom.view

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.ethan.android.notepad.R
import com.ethan.android.notepad.extension.dpF
import com.ethan.android.notepad.utils.dpI
import com.ethan.android.notepad.utils.setAlpha

/**
 * 图片对比动画（带文字）
 */
@Composable
fun ImageWithTextContrastView() {
    val context = LocalContext.current
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val animationPlayed = remember { mutableIntStateOf(0) }
        val anim = remember { Animatable(0f) }

        val endAnimSwitch = remember { mutableStateOf(false) }
        val endAnimValue = animateFloatAsState(if (endAnimSwitch.value) 0F else 1F, label = "",
            animationSpec = tween(durationMillis = 300, easing = CubicBezierEasing(0.72F, 0F, 0.27F, 1F)))
        LaunchedEffect(animationPlayed.intValue) {
            if (animationPlayed.intValue < 2) {
                anim.animateTo(targetValue = 1f,
                    animationSpec = tween(durationMillis = 2000, easing = CubicBezierEasing(0.72F, 0F, 0.27F, 1F)))
                anim.animateTo(targetValue = 0f,
                    animationSpec = tween(durationMillis = 2000, easing = CubicBezierEasing(0.72F, 0F, 0.27F, 1F)))
                animationPlayed.intValue += 1
            } else {
                anim.animateTo(targetValue = 1f,
                    animationSpec = tween(durationMillis = 2000, easing = CubicBezierEasing(0.72F, 0F, 0.27F, 1F)))
                endAnimSwitch.value = true
            }
        }
        val clipWidth = 16.dpF + ((maxWidth.value.dpF - ((16.dpF * 1.2F) * endAnimValue.value)) * anim.value)
        AsyncImage(model = ImageRequest.Builder(context).data(R.mipmap.before_content).decoderFactory(if (SDK_INT >= 28) {
            AnimatedImageDecoder.Factory()
        } else {
            GifDecoder.Factory()
        }).build(), "", modifier = Modifier
            .fillMaxSize()
            .scale(1f + (0.2F * anim.value)), contentScale = ContentScale.Crop)

        AsyncImage(model = ImageRequest.Builder(context).data(R.mipmap.after_content).decoderFactory(if (SDK_INT >= 28) {
            AnimatedImageDecoder.Factory()
        } else {
            GifDecoder.Factory()
        }).build(), "", modifier = Modifier
            .fillMaxSize()
            .scale(1.2f - (0.2F * anim.value))
            .drawWithContent {
                clipRect(left = 0f, top = 0f, right = clipWidth, bottom = size.height) {
                    this@drawWithContent.drawContent()
                }
                drawLine(
                    brush = Brush.verticalGradient(0F to Color.White.setAlpha(0.0F), 0.5F to Color.White.setAlpha(0.8F),
                        1F to Color.White.setAlpha(0.0F)),
                    start = Offset(clipWidth, 0F), end = Offset(clipWidth, size.height),
                    strokeWidth = 2.dpF,
                )
            }, contentScale = ContentScale.Crop)

        val textBoxSize = remember { mutableStateOf(IntSize.Zero) }
        androidx.compose.material.Text("处理后",
            modifier = Modifier
                .onGloballyPositioned { textBoxSize.value = it.size }
                .graphicsLayer {
                    translationX = clipWidth - textBoxSize.value.width - 6.dpI
                    translationY = maxHeight.toPx() * 0.8F
                    alpha = (anim.value) * endAnimValue.value
                }, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
        androidx.compose.material.Text("处理前", modifier = Modifier.graphicsLayer {
            translationX = clipWidth + 6.dpF
            translationY = maxHeight.toPx() * 0.8F
            alpha = (1 - anim.value) * endAnimValue.value
        }, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}