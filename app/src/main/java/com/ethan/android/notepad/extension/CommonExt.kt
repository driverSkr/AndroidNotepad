package com.ethan.android.notepad.extension

import android.app.Activity
import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity

import com.blankj.utilcode.util.AppUtils
import com.google.gson.Gson
//import com.hitpaw.file.LogWriter

import kotlinx.coroutines.MainScope

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

val Any.TAG: String
    get() {
        return if (!javaClass.isAnonymousClass) {
            val name = javaClass.simpleName
            if (name.length <= 23) name else name.substring(0, 23)// first 23 chars
        } else {
            val name = javaClass.name
            if (name.length <= 23) name else name.substring(name.length - 23, name.length)// last 23 chars
        }
    }

val gson = Gson()


val Number.dp: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()
val Number.dpW: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()
val Number.dpF: Float get() = (toInt() * Resources.getSystem().displayMetrics.density)
val Number.px: Int get() = (toInt() / Resources.getSystem().displayMetrics.density).toInt()
val Number.pxF: Float get() = (toInt() / Resources.getSystem().displayMetrics.density)

// 转sp
val Number.sp: Int get() = (toInt() * Resources.getSystem().displayMetrics.scaledDensity).toInt()

val Number.spF: Float get() = (toInt() * Resources.getSystem().displayMetrics.scaledDensity)

//文件大小格式化
fun Long.formatBytes(decimals: Int = 2): String {
    if (this == 0L || this == -1L) return "0 B"
    val suffixes = arrayOf("B", "KB", "MB", "GB", "TB")
    val i = floor(log10(this.toDouble()) / log10(1000.0)).toInt().coerceAtMost(suffixes.size - 1)
    val size = this / 1000.0.pow(i)
    return String.format("%.${decimals}f %s", size, suffixes[i])
}

fun Rect.getArea(): Int {
    return width() * height()
}

fun Pair<Number, Number>.scaleThanWH(width: Int, height: Int): Float {
    val widthScale = this.first.toFloat() / width.toFloat()
    val heightScale = this.first.toFloat() / height.toFloat()
    return minOf(widthScale, heightScale)
}

fun String?.getTemplateName(): String {
    return this?.trimEnd()?.replace(" ", "_") ?: "TemplateEmpty"
}

fun String?.getBannerName(): String {
    return this?.trimEnd()?.replace(" ", "_") ?: "KindEmpty"
}

fun String?.getMainBannerName(): String {
    return this?.trimEnd()?.replace(" ", "_") ?: "BannerEmpty "
}

fun Float.to2f(): String {
    return String.format(Locale.US, "%.2f", this)
}


fun View.addPaddingBottom(px: Int) {
    this.setPadding(
        this.paddingLeft,
        this.paddingTop,
        this.paddingRight,
        this.paddingBottom + px,
                   )
}

/**
 * @self 允许的最大范围尺寸
 * @param width dst 宽
 * @param height dst 高
 */
fun Rect.scaleThanWH(width: Int, height: Int): Float {
    val widthScale = this.width() / width.toFloat()
    val heightScale = this.height() / height.toFloat()
    return minOf(widthScale, heightScale)
}



fun roundToNearestMultiple(size: Long, multiple: Int): Long {
// 将 value 调整为最接近的 multiple 的倍数
    return (size / multiple) * multiple
}



// 将毫秒转换为 "00:00" 格式的时间字符串
fun Long.formatDuration(): String {
    val totalSeconds = this / 1000 // 转换为秒
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}




fun checkSecondaryTemplateVersion() {
    val appVersionName = AppUtils.getAppVersionName().split(".").map {
        it.toIntOrNull() ?: 0
    }

}

fun <T> MutableList<T>.moveToFirst(element: T?) {
    val indexOfElement = indexOf(element)
    if (indexOfElement != -1) {
        val removedElement = removeAt(indexOfElement)
        add(0, removedElement)
    }
}

fun TextView.setTextDrawables(context: Context, leftRs: Int? = null, topRs: Int? = null, rightRs: Int? = null, bottomRs: Int? = null) {
    val l = if (leftRs != null) ResourcesCompat.getDrawable(context.resources, leftRs, null) else null
    val t = if (topRs != null) ResourcesCompat.getDrawable(context.resources, topRs, null) else null
    val r = if (rightRs != null) ResourcesCompat.getDrawable(context.resources, rightRs, null) else null
    val b = if (bottomRs != null) ResourcesCompat.getDrawable(context.resources, bottomRs, null) else null
    this.setCompoundDrawablesWithIntrinsicBounds(l, t, r, b)
}

fun TextView.setTextDrawablesBounds(padding: Int) {
    this.compoundDrawablePadding = padding
}


fun fileRate(size: Long, now: Long, node: Long): Float {
    Log.e("TAG", "eventHandle now - node: " + (now - node))
    Log.e("TAG", "eventHandle size: " + size)
    Log.e("TAG", "eventHandle fileRate: " + (now - node) / 1024F)
    return size / ((now - node) / 1024F).coerceAtLeast(1F)
}


fun Long.roundingMode2(): BigDecimal? {
    val d = this.toDouble() / (1024 * 1024)
    val number = BigDecimal(d)
    Log.e("TAG", "eventHandle:roundingMode2 $d")
    return number.setScale(2, RoundingMode.HALF_EVEN)
}

fun Long.toSizeMB(): String {
    val d = this.toDouble() / (1024 * 1024)
    val number = BigDecimal(d)
    val scaledNumber = number.setScale(2, RoundingMode.HALF_EVEN)
    Log.e("TAG", "eventHandle:toSizeMB $this")
    Log.e("TAG", "eventHandle:toSizeMB $d")
    return "${scaledNumber}MB"
}

fun Long.toSizeM(): String {
    val d = this.toDouble() / (1024 * 1024)
    val number = BigDecimal(d)
    val scaledNumber = number.setScale(2, RoundingMode.HALF_EVEN)
    Log.e("TAG", "eventHandle:toSizeMB $this")
    Log.e("TAG", "eventHandle:toSizeMB $d")
    return "${scaledNumber}M"
}

fun String.truncateString(maxLength: Int = 4000): String {
    // 判断字符串是否超过最大长度，若超过则截取
    return if (length > maxLength) {
        this.substring(0, maxLength)
    } else {
        this
    }
}

fun Context?.isAvailable(): Boolean {
    if (this == null) {
        return false
    } else if (this !is Application) {
        if (this is FragmentActivity) {
            return !this.isDestroyed
        } else if (this is Activity) {
            return !this.isDestroyed
        }
    }
    return true
}


val commonScope = MainScope()



//fun isTestVersion(): Boolean {
//    val count = LogWriter.version_name!!.count { it == '.' }
//    Log.e("count", "append: $count")
//    return count >= 3
//}

// 版本号拆分为数字数组进行比较
fun compareVersions(version1: String, version2: String): Boolean {
    val v1 = version1.split(".").map { it.toInt() }
    val v2 = version2.split(".").map { it.toInt() }

    for (i in 0 until minOf(v1.size, v2.size)) {
        if (v1[i] != v2[i]) {
            return v1[i] > v2[i]
        }
    }
    return v1.size >= v2.size
}

fun readFromClipboard(context: Context): String? {
    // 获取系统剪切板服务
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // 检查剪切板中是否有数据
    if (!clipboard.hasPrimaryClip()) {
        return null
    }

    // 获取剪切板中的数据
    val clipData = clipboard.primaryClip
    if (clipData != null && clipData.itemCount > 0) {
        // 返回第一个数据项的文本
        val clipText = clipData.getItemAt(0).text
        return clipText?.toString()
    }
    return null
}



