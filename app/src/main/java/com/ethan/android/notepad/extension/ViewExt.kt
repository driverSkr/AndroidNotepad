package com.ethan.android.notepad.extension

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.GONE
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewGroup.VISIBLE
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.graphics.values
import androidx.core.view.doOnLayout
import com.blankj.utilcode.util.ClickUtils
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 尺寸回调
 */
fun View.onInitialized(onInit: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (isShown) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                onInit()
            }
        }
    })
}

fun View.antiShakeClick(clickEvent: (view: View) -> Unit) {
    ClickUtils.applySingleDebouncing(this) {
        clickEvent.invoke(it)
    }
}

fun View.antiShakeClick(time: Long, clickEvent: (view: View) -> Unit) {
    ClickUtils.applySingleDebouncing(this, time) {
        clickEvent.invoke(it)
    }
}

/**
 * 设置圆角矩形
 */
fun View.setViewFillet(round: Float) {
    this.outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(Rect(0, 0, view!!.width, view.height), round)
        }
    }
    this.clipToOutline = true
}

/**
 * 设置圆角矩形带Stroke
 */
fun View.setViewFilletWithStroke(round: Float, strokeColor: Int, strokeWidth: Float) {
    this.outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(Rect(0, 0, view!!.width, view.height), round)
        }
    }
    this.clipToOutline = true
    // 设置边框
    val shapeDrawable = ShapeDrawable(RoundRectShape(floatArrayOf(round, round, round, round, round, round, round, round), null, null))
    shapeDrawable.paint.color = strokeColor
    shapeDrawable.paint.style = Paint.Style.STROKE
    shapeDrawable.paint.strokeWidth = strokeWidth
    this.foreground = shapeDrawable
}


/**
 * 矩阵连接
 */
fun Matrix.matrixConcat(secondMatrix: Matrix): Matrix {
    val tMatrix = Matrix(this)
    val sMatrix = Matrix(secondMatrix)
    tMatrix.postConcat(sMatrix)
    return tMatrix
}

/**
 * 获得自身的反矩阵
 */
fun Matrix.invertSelf(): Matrix {
    val invMatrix = Matrix()
    this.invert(invMatrix)
    return invMatrix
}

fun Matrix.mapPointF(point: PointF): PointF {
    val p = floatArrayOf(point.x, point.y)
    this.mapPoints(p)
    return PointF(p[0], p[1])
}

fun Matrix.mapPointFSelf(point: PointF) {
    val p = floatArrayOf(point.x, point.y)
    this.mapPoints(p)
    point.x = p[0]
    point.y = p[1]
}

val Matrix.rotation: Float
    get() {
        return atan2(
            values()[Matrix.MSKEW_X],
            values()[Matrix.MSCALE_X],
        ) * (180F / Math.PI.toFloat())
    }
val Matrix.scale: Float
    get() {
        return sqrt(values()[Matrix.MSCALE_X].pow(2) + values()[Matrix.MSKEW_Y].pow(2))
    }

/**
 * 经历多层映射的rect
 */
fun Rect.multiMapRect(vararg matrixList: Matrix): RectF {
    val rect = RectF(this)
    matrixList.forEach {
        it.mapRect(rect)
    }
    return rect
}

fun RectF.multiMapRect(vararg matrixList: Matrix): RectF {
    val rect = RectF(this)
    matrixList.forEach {
        it.mapRect(rect)
    }
    return rect
}

fun View.setTopMargin(px: Int) {
    val marginLayoutParams = this.layoutParams as MarginLayoutParams
    marginLayoutParams.topMargin = px
    this.layoutParams = marginLayoutParams
}

fun View.setBottomMargin(px: Int) {
    val marginLayoutParams = this.layoutParams as MarginLayoutParams
    marginLayoutParams.bottomMargin = px
    this.layoutParams = marginLayoutParams
}

fun View.setStartMargin(px: Int) {
    val marginLayoutParams = this.layoutParams as MarginLayoutParams
    marginLayoutParams.marginStart = px
    this.layoutParams = marginLayoutParams
}

fun View.setEndMargin(px: Int) {
    val marginLayoutParams = this.layoutParams as MarginLayoutParams
    marginLayoutParams.marginEnd = px
    this.layoutParams = marginLayoutParams
}

fun View.setHeight(px: Int) {
    val layoutParams = this.layoutParams
    layoutParams.height = px
    this.layoutParams = layoutParams
}

fun View.setWidth(px: Int) {
    val layoutParams = this.layoutParams
    layoutParams.width = px
    this.layoutParams = layoutParams
}

fun View.setWH(pxW: Int, pxH: Int) {
    val layoutParams = this.layoutParams
    layoutParams.width = pxW
    layoutParams.width = pxH
    this.layoutParams = layoutParams
}

fun View.addPaddingTop(px: Int) {
    this.setPadding(
        this.paddingLeft,
        this.paddingTop + px,
        this.paddingRight,
        this.paddingBottom,
    )
}

//fun View.addPaddingBottom(px: Int) {
//    this.setPadding(
//        this.paddingLeft,
//        this.paddingTop,
//        this.paddingRight,
//        this.paddingBottom + px,
//    )
//}

fun View.setPaddingBottom(px: Int) {
    this.setPadding(
        this.paddingLeft,
        this.paddingTop,
        this.paddingRight,
        px,
    )
}


/**
 * @param position 位置
 *  0-----1
 *  |     |
 *  |     |
 *  3-----2
 */
fun ViewGroup.addPendant(image: Bitmap, position: Int, wide: Int, high: Int, xTrans: Float, yTrans: Float) {
    val pendantView = ImageView(this.context)
    val layoutParams = FrameLayout.LayoutParams(wide, high)
    pendantView.layoutParams = layoutParams
    pendantView.setImageBitmap(image)
    this.addView(pendantView)

    when (position) {
        0 -> {
            this.doOnLayout {
                pendantView.translationX = xTrans
                pendantView.translationY = yTrans
            }
        }

        1 -> {
            this.doOnLayout {
                pendantView.translationX = this.width - wide - xTrans
                pendantView.translationY = yTrans
            }
        }

        2 -> {
            this.doOnLayout {
                pendantView.translationX = this.width - wide - xTrans
                pendantView.translationY = this.height - high - yTrans
            }
        }

        3 -> {
            this.doOnLayout {
                pendantView.translationX = xTrans
                pendantView.translationY = this.height - high - yTrans
            }
        }

        else -> {}
    }
}


// 扩展函数，将视图从隐藏到可见动画
fun View.animaAlpha2Visible(duration: Long) {
    this.visibility = View.VISIBLE
    val animator = ObjectAnimator.ofFloat(this, "alpha", 0F, 1F)
    animator.doOnEnd {
        this.visibility = VISIBLE
    }
    animator.duration = duration
    animator.start()
}

fun View.animaAlpha2Gone(duration: Long) {
    this.visibility = View.VISIBLE
    val animator = ObjectAnimator.ofFloat(this, "alpha", 1F, 0F)
    animator.doOnEnd {
        this.visibility = GONE
    }
    animator.duration = duration
    animator.start()
}

fun View.addMarginTop(px: Int) {
    (this.layoutParams as MarginLayoutParams).topMargin += px
}

fun View.addMarginBottom(px: Int) {
    (this.layoutParams as MarginLayoutParams).bottomMargin += px
}

fun View.addMarginStart(px: Int) {
    (this.layoutParams as MarginLayoutParams).marginStart += px
}

fun View.addMarginEnd(px: Int) {
    (this.layoutParams as MarginLayoutParams).marginEnd += px
}