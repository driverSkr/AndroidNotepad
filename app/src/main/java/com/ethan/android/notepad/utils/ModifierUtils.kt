package com.ethan.android.notepad.utils

import android.content.Context
import android.graphics.Rect
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.core.content.ContextCompat
import com.ethan.android.notepad.extension.commonScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

private const val TAG = "ModifierUtils"

private enum class ButtonState { Pressed, Idle }

fun Modifier.bounceClick(enabled: Boolean = true, delay: Long = 500, @FloatRange(from = 0.0, to = 1.0) minScale: Float = 0.95f, onClick: () -> Unit) = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) minScale else 1f, label = "bounceClick", animationSpec = tween(300))
    var clicked by remember {
        mutableStateOf(!enabled)
    }
    LaunchedEffect(key1 = clicked, block = {
        if (clicked) {
            delay(delay)
            clicked = !clicked
        }
    })
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(enabled = if (enabled) !clicked else false, interactionSource = remember { MutableInteractionSource() }, indication = null) {
            clicked = !clicked
            onClick.invoke()
        }
        .pointerInput(buttonState) {
            Log.i(TAG, "bounceClick: $buttonState")
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}


fun Modifier.animaBounceClick(enabled: Boolean = true, delay: Long = 500, @FloatRange(from = 0.0, to = 1.0) minScale: Float = 0.9f, onClick: () -> Unit) = composed {
    val buttonState = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (buttonState.value) minScale else 1f, label = "bounceClick", animationSpec = tween(70))
    var clicked by remember {
        mutableStateOf(!enabled)
    }
    LaunchedEffect(key1 = clicked, block = {
        if (clicked) {
            delay(delay)
            clicked = !clicked
        }
    })

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(enabled = if (enabled) !clicked else false, interactionSource = remember { MutableInteractionSource() }, indication = null) {
            clicked = !clicked
            buttonState.value = true
            commonScope.launch {
                delay(70)
                buttonState.value = false
                delay(50)
                onClick.invoke()
            }
        }
}

fun Modifier.downState(isDown: MutableState<Boolean> = mutableStateOf(false)) = composed {
    var pointerModel by remember { mutableStateOf(true) }
    then(Modifier.pointerInput(pointerModel) {
        awaitPointerEventScope {
            awaitFirstDown(false)
            isDown.value = true
            waitForUpOrCancellation()
            isDown.value = false
            pointerModel = !pointerModel
        }
    })
}

fun Modifier.antiShakeClick(enabled: Boolean = true, delay: Long = 500, onClick: () -> Unit) = composed {
    var clicked by remember {
        mutableStateOf(!enabled)
    }
    LaunchedEffect(key1 = clicked, block = {
        if (clicked) {
            delay(delay)
            clicked = !clicked
        }
    })

    then(Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, if (enabled) !clicked else false) {
        clicked = !clicked
        onClick.invoke()
    })
}

fun Modifier.commonClick(onClick: () -> Unit) = composed {
    then(Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
        onClick.invoke()
    })
}

fun Modifier.draw9Patch(
    context: Context,
    @DrawableRes ninePatchRes: Int,
                       ) = this.drawBehind {
    drawIntoCanvas {
        ContextCompat.getDrawable(context, ninePatchRes)?.let { ninePatch ->
            ninePatch.run {
                bounds = Rect(0, 0, size.width.toInt(), size.height.toInt())
                draw(it.nativeCanvas)
            }
        }
    }
}


/**
 * 调整Compose的 invisible
 */
fun Modifier.invisible(visible: Boolean): Modifier {
    return layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            if (visible) {
                placeable.placeRelative(0, 0)
            }
        }
    }
}

val nullInteractionSource = object : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) {}

    override fun tryEmit(interaction: Interaction) = true
}

