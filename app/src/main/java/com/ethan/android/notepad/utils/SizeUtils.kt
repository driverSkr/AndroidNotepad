package com.ethan.android.notepad.utils

import android.graphics.Rect
import androidx.compose.ui.unit.Dp
import com.blankj.utilcode.util.SizeUtils

/**
 * dp2px
 */
val Number.dpF: Float
    get() = SizeUtils.dp2px(this.toFloat()).toFloat()

val Number.dpI: Int
    get() = SizeUtils.dp2px(this.toFloat())

/**
 * px2dp
 */
val Number.pxI: Int
    get() = SizeUtils.px2dp(this.toFloat())

val Number.pxF: Float
    get() = SizeUtils.px2dp(this.toFloat()).toFloat()

/**
 * sp2px
 */
val Number.spI: Int
    get() = SizeUtils.sp2px(this.toFloat())
val Number.spF: Float
    get() = SizeUtils.sp2px(this.toFloat()).toFloat()

/**
 * 根据Dp变成Compose的dp
 */
val Number.dpCps: Dp
    get() = Dp(this.toFloat())


fun Rect.scale2target(width: Int, height: Int): Float {
    val widthScale = this.width() / width.toFloat()
    val heightScale = this.height() / height.toFloat()
    return minOf(widthScale, heightScale)
}