package com.ethan.android.notepad.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.util.SizeF
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withClip
import androidx.core.graphics.withMatrix
import androidx.core.view.doOnLayout
import com.almeros.android.multitouch.MoveGestureDetector
import com.blankj.utilcode.util.VibrateUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.extension.dpF
import com.ethan.android.notepad.extension.invertSelf
import com.ethan.android.notepad.model.ScaleGestureDetector
import com.ethan.android.notepad.utils.scale2target
import com.google.android.material.animation.MatrixEvaluator

private const val TAG = "CompareV2View"

class CompareV2View : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var imgContrastHandleAlpha = 1F
    private val imgContrastHandle: Bitmap by lazy {
        BitmapFactory.decodeResource(context.resources, R.mipmap.compareview_tubm_icon)
    }


    private var imgSize: Size = Size(0, 0)
    private var imgOffset: SizeF = SizeF(0F, 0F)
    private var syncTotalMatrix = Matrix()
    private var beforeImage: Bitmap? = null
    private var beforeImageMatrix: Matrix = Matrix()
    private var afterImage: Bitmap? = null
    private var afterImageMatrix: Matrix = Matrix()
    private var bitmapPaint: Paint = Paint().apply {
        isAntiAlias = true
    }
    private var handlerPaint: Paint = Paint().apply {
        isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i(TAG, "onSizeChanged: 当前View宽高为 $width $height")
    }

    private var clipRect = RectF()
    private var contrastLineProgress = 0.5F
        set(value) {
            field = value
            refClipRect()
            invalidate()
        }

    private var contrastLineHPosition = 0.7F
    private var contrastLineStroke = 2.dpF
    private var contrastLinePen = Paint().apply {
        color = Color.WHITE
        strokeWidth = contrastLineStroke
    }

    private fun refClipRect() {
        clipRect.set(width * contrastLineProgress, 0F, width.toFloat(), height.toFloat())
        syncTotalMatrix.invertSelf().mapRect(clipRect)
    }

    fun setImage(beforeImage: Bitmap, afterImage: Bitmap) {
        this.doOnLayout {
            Log.i(TAG, "setImage: 设置图片,当前View宽高为 $width $height")
            this.afterImage = afterImage
            this.beforeImage = beforeImage
            val bW = this.beforeImage?.width ?: return@doOnLayout
            val bH = this.beforeImage?.height ?: return@doOnLayout
            val aW = this.afterImage?.width ?: return@doOnLayout
            val aH = this.afterImage?.height ?: return@doOnLayout
            val w = this.width
            val h = this.height

            val bScale = Rect(0, 0, w, h).scale2target(bW, bH)
            beforeImageMatrix.reset()
            beforeImageMatrix.postScale(bScale, bScale)
            beforeImageMatrix.postTranslate((w - (bW * bScale)) / 2, (h - bH * bScale) / 2)
            Log.i(TAG, "setImage: bScale -> $bScale")

            val aScale = Rect(0, 0, w, h).scale2target(aW, aH)
            afterImageMatrix.reset()
            afterImageMatrix.postScale(aScale, aScale)
            val transX = (w - (aW * aScale)) / 2
            val tranY = (h - aH * aScale) / 2
            afterImageMatrix.postTranslate(transX, tranY)
            Log.i(TAG, "setImage: aScale -> $aScale")

            imgSize = Size((aW * aScale).toInt(), (aH * aScale).toInt())
            imgOffset = SizeF(transX, tranY)
            Log.i(TAG, "setImage: 图片缩放到 $imgSize")
            refClipRect()
            invalidate()
        }

    }

    fun setImage(beforeImage: String, afterImage: String) {
        val before = BitmapFactory.decodeFile(beforeImage)
        val after = BitmapFactory.decodeFile(afterImage)
        setImage(before, after)
    }

    private var moveGestureDetector = MoveGestureDetector(context, object : MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector?): Boolean {
            detector?.let {
                val d = detector.focusDelta
                syncTotalMatrix.postTranslate(d.x, d.y)
                refClipRect()
                invalidate()
            }
            return true
        }

    })

    private var scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            Log.i(TAG, "onScale: ${detector.scaleFactor}")
            syncTotalMatrix.postScale(detector.scaleFactor, detector.scaleFactor, detector.focusX, detector.focusY)
            refClipRect()
            invalidate()
            return true
        }

    }).apply {
        isQuickScaleEnabled = false
    }

    private var isDownInLine = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (event == null) return true
        if (!isDownInLine) {
            moveGestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDownInLine = downInLine(event)
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDownInLine) {
                    contrastLineProgress = (event.x / width).coerceIn(0.1F, 0.9F)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                animFixSyncMatrix()
                if (isDownInLine) animateLineAndHandle(1.dpF, 2.dpF, 0F, 1F)
                isDownInLine = false
            }

            else -> {}
        }
        return true
    }

    private fun downInLine(event: MotionEvent): Boolean {
        val lineX = width * contrastLineProgress
        val startX = lineX - 32.dpF
        val endX = (lineX + 32.dpF) + imgContrastHandle.width
        val isInLine = event.x in startX..endX
        if (isInLine) {
            VibrateUtils.vibrate(10)
            animateLineAndHandle(2.dpF, 1.dpF, 1F, 0F)
        }
        return isInLine
    }

    private fun animateLineAndHandle(startLineStroke: Float, endLineStroke: Float, startHandleAlpha: Float, endHandleAlpha: Float) {
        ValueAnimator.ofFloat(startLineStroke, endLineStroke).apply {
            duration = 300
            addUpdateListener {
                contrastLineStroke = it.animatedValue as Float
                contrastLinePen.strokeWidth = contrastLineStroke
                invalidate()
            }
            start()
        }
        ValueAnimator.ofFloat(startHandleAlpha, endHandleAlpha).apply {
            duration = 300
            addUpdateListener {
                imgContrastHandleAlpha = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawImage(canvas)
        drawLine(canvas)
    }


    private fun drawImage(canvas: Canvas) {
        val totalMatrixSave = canvas.save()
        canvas.concat(syncTotalMatrix)
        canvas.withMatrix(beforeImageMatrix) {
            beforeImage?.let { canvas.drawBitmap(it, 0F, 0F, bitmapPaint) }
        }
        canvas.withClip(clipRect) {
            canvas.withMatrix(afterImageMatrix) {
                afterImage?.let { canvas.drawBitmap(it, 0F, 0F, bitmapPaint) }
            }
        }
        canvas.restoreToCount(totalMatrixSave)
    }

    private fun drawLine(canvas: Canvas) {
        val lineX = width * contrastLineProgress
        canvas.drawLine(lineX, 0F, lineX, height.toFloat(), contrastLinePen)
        handlerPaint.alpha = (255 * imgContrastHandleAlpha).toInt()
        canvas.drawBitmap(imgContrastHandle, lineX - imgContrastHandle.width / 2F, height * contrastLineHPosition, handlerPaint)
    }

    /**
     * 动画修复矩阵位置
     */
    private fun animFixSyncMatrix() {
        val srcMatrix = Matrix(syncTotalMatrix)
        val dstMatrix = Matrix(syncTotalMatrix)
        getFixMatrix(srcMatrix, dstMatrix)
        val animator = ValueAnimator.ofObject(MatrixEvaluator(), srcMatrix, dstMatrix)
        animator.duration = 300
        animator.addUpdateListener {
            val mat = it.animatedValue as Matrix
            syncTotalMatrix = mat
            refClipRect()
            invalidate()
        }
        animator.start()
    }

    private fun getFixMatrix(srcMatrix: Matrix, dstMatrix: Matrix) {
        val maxScale = 8F
        val minScale = 0.8F
        val imageOriginRect = RectF(imgOffset.width, imgOffset.height, imgOffset.width + imgSize.width, imgOffset.height + imgSize.height)
        val imageFixDstRect = RectF(imageOriginRect)
        val scale2target = Rect(0, 0, width, height).scale2target(imgSize.width, imgSize.height)
        val xOffset = (width - (imgSize.width * scale2target)) / 2F
        val yOffset = (height - (imgSize.height * scale2target)) / 2F
        imageFixDstRect.set(xOffset, yOffset, xOffset + (imgSize.width * scale2target), yOffset + (imgSize.height * scale2target))

        val scaleFitImageRect = RectF()
        srcMatrix.mapRect(scaleFitImageRect, imageOriginRect)

        // 判断放大越界
        if (scaleFitImageRect.width() > imageFixDstRect.width() * maxScale) {
            val scale = (imageFixDstRect.width() * maxScale) / scaleFitImageRect.width()
            Log.i(TAG, "animFixSyncMatrix: 过大${scale}")
            dstMatrix.postScale(scale, scale, width / 2F, height / 2F)
        } // 判断缩小越界
        if (scaleFitImageRect.width() < imageFixDstRect.width() * minScale) {
            val scale = (imageFixDstRect.width() * minScale) / scaleFitImageRect.width()
            Log.i(TAG, "animFixSyncMatrix: 过小${scale}")
            dstMatrix.postScale(scale, scale, width / 2F, height / 2F)
        }

        val transFitImageRect = RectF()
        dstMatrix.mapRect(transFitImageRect, imageOriginRect)
        Log.i(TAG, "getFixMatrix: 原图尺寸为${imageOriginRect} 映射位置为${transFitImageRect}")
        val limitTransW = imageFixDstRect.width() * minScale
        val limitTransH = imageFixDstRect.height() * minScale

        val limitTransXOffset = (width - limitTransW) / 2F
        val limitTransYOffset = (height - limitTransH) / 2F
        val imageFixMinDstRect = RectF(limitTransXOffset, limitTransYOffset, limitTransXOffset + limitTransW,
            limitTransYOffset + limitTransH)
        Log.i(TAG, "getFixMatrix: 修复位置为$imageFixMinDstRect")
        val dx = when {
            transFitImageRect.left > imageFixMinDstRect.left -> imageFixMinDstRect.left - transFitImageRect.left
            transFitImageRect.right < imageFixMinDstRect.right -> imageFixMinDstRect.right - transFitImageRect.right
            else -> 0F
        }

        val dy = when {
            transFitImageRect.top > imageFixMinDstRect.top -> imageFixMinDstRect.top - transFitImageRect.top
            transFitImageRect.bottom < imageFixMinDstRect.bottom -> imageFixMinDstRect.bottom - transFitImageRect.bottom
            else -> 0F
        }

        if (dx != 0F || dy != 0F) {
            dstMatrix.postTranslate(dx, dy)
            Log.i(TAG, "animFixSyncMatrix: dx = $dx, dy = $dy")
        }
    }


}