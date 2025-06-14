package com.ethan.android.notepad.utils


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb



fun Color.setAlpha(alpha: Float): Color {
    return Color(this.red, this.green, this.blue, alpha)
}

fun generateRandomColor(): Color {
    val red = (0..255).random()
    val green = (0..255).random()
    val blue = (0..255).random()
    return Color(red, green, blue)
}

fun Color.toAGColor() = toArgb().run { android.graphics.Color.argb(alpha, red, green, blue) }

fun Color.toHexCodeWithAlpha(): String {
    val alpha = this.alpha * 255
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255
    return String.format("#%02x%02x%02x%02x", alpha.toInt(), red.toInt(), green.toInt(), blue.toInt())
}