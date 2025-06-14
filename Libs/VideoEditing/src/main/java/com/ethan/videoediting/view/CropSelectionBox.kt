package com.ethan.videoediting.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.graphics.values
import androidx.core.view.doOnLayout

private const val TAG = "CropSelectionBox"

/**
 * @author  PengHaiChen
 * @date    2023/7/3 17:56
 * @email   penghaichen@tenorshare.cn
 * 用来选择视频裁切范围的布局
 */
class CropSelectionBox : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    var dragDropMode: Int = CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO

    /**
     * 开发帮助,显示辅助线
     */
    private var isDevHelp = false

    private val selectPaint = Paint().apply {
        color = Color.parseColor("#FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 10F
        this.isAntiAlias = true
    }
    private val sizeRect = RectF()
    private var limitRect = Rect()

    private val cropMatrix = Matrix()
    private var limitPaint = Paint().also {
        it.color = Color.BLUE
        it.style = Paint.Style.STROKE
        it.strokeWidth = 10F

    }
    private var cropPaint = Paint().also {
        it.color = Color.GREEN
        it.style = Paint.Style.STROKE
        it.strokeWidth = dp2px(1.5F)
    }
    private val selectPath = Path()
    private val cornerSize = dp2px(24F) // 边角大小
    private val cornerTouchSize = dp2px(24F) // 边角大小

    private var smallestEdge = dp2px(60F)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawHollowPath(canvas)
        canvas.save()
        canvas.concat(cropMatrix)
        drawSelectBox(canvas)
        canvas.restore()
        if (isDevHelp) {
            canvas.drawRect(limitRect, limitPaint)
            canvas.drawRect(cropRect, cropPaint)
        }
    }

    private var sizeRectMap = RectF()
    private var maskPathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private var maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.parseColor("#30000000")
    }

    private fun drawHollowPath(canvas: Canvas) {
        val saveLayer = canvas!!.saveLayer(0F, 0F, width.toFloat(), height.toFloat(), null)
        canvas.drawRect(limitRect, maskPaint)
        cropMatrix.mapRect(sizeRectMap, sizeRect)
        canvas.drawRect(sizeRectMap, maskPathPaint)
        canvas.restoreToCount(saveLayer)
    }

    var roundedCorners = dp2px(16F * 2)

    private fun drawSelectBox(canvas: Canvas) {
        selectPath.reset()

        // 绘制左上角边角
        selectPath.reset()
        selectPath.moveTo(sizeRect.left, sizeRect.top + cornerSize)
        selectPath.lineTo(sizeRect.left, sizeRect.top)
        selectPath.lineTo(sizeRect.left + cornerSize, sizeRect.top)

        // 绘制右上角边角
        selectPath.moveTo(sizeRect.right - cornerSize, sizeRect.top)
        selectPath.lineTo(sizeRect.right, sizeRect.top)
        selectPath.lineTo(sizeRect.right, sizeRect.top + cornerSize)

        // 绘制右下角边角
        selectPath.moveTo(sizeRect.right, sizeRect.bottom - cornerSize)
        selectPath.lineTo(sizeRect.right, sizeRect.bottom)
        selectPath.lineTo(sizeRect.right - cornerSize, sizeRect.bottom)

        // 绘制左下角边角
        selectPath.moveTo(sizeRect.left + cornerSize, sizeRect.bottom)
        selectPath.lineTo(sizeRect.left, sizeRect.bottom)
        selectPath.lineTo(sizeRect.left, sizeRect.bottom - cornerSize)
        canvas.drawPath(selectPath, selectPaint)
    }

    private var centerX: Float = 0F;
    private var centerY: Float = 0F;
    private var aspectRatio = 1F

    /**
     * 按照给定尺寸位移到中心
     * 会清空[cropMatrix]
     */
    fun setSelectSize(width: Int, height: Int, limitRect: Rect) {
        this.doOnLayout {
            cropMatrix.reset()
            this.limitRect = limitRect
            centerX = (limitRect.width() - width) / 2F
            centerY = (limitRect.height() - height) / 2F
            cropMatrix.postTranslate(limitRect.left + centerX, limitRect.top + centerY)
            sizeRect.set(0F, 0F, width.toFloat(), height.toFloat())
            aspectRatio = width.toFloat() / height.toFloat()
            invalidate()
        }
    }

    fun moveCropBox(xOffset: Float, yOffset: Float) {
        cropMatrix.postTranslate(xOffset, yOffset)
        val dstRectF = RectF()
        cropMatrix.mapRect(dstRectF, sizeRect)
        if (dstRectF.left < limitRect.left || dstRectF.top < limitRect.top || dstRectF.right > limitRect.right || dstRectF.bottom > limitRect.bottom) { // 越界
            val safeXOffset = when {
                dstRectF.left < limitRect.left -> limitRect.left - dstRectF.left
                dstRectF.right > limitRect.right -> limitRect.right - dstRectF.right
                else -> 0f
            }
            val safeYOffset = when {
                dstRectF.top < limitRect.top -> limitRect.top - dstRectF.top
                dstRectF.bottom > limitRect.bottom -> limitRect.bottom - dstRectF.bottom
                else -> 0f
            }
            cropMatrix.postTranslate(safeXOffset, safeYOffset)
        } else { // 安全
        }
        invalidate()
    }

    fun moveCropCorner(xOffset: Float, yOffset: Float, selectCorner: Int) {

        when (selectCorner) {
            1 -> {
                sizeRect.left += xOffset
                sizeRect.top += yOffset
                limitTop()
                limitRectLT()
                limitSmallestLT()
            }

            2 -> {
                sizeRect.right += xOffset
                sizeRect.top += yOffset
                limitTop()
                limitRectRT()
                limitSmallestRT()
            }

            3 -> {
                sizeRect.right += xOffset
                sizeRect.bottom += yOffset
                limitBottom()
                limitRectRB()
                limitSmallestRB()
            }

            4 -> {
                sizeRect.left += xOffset
                sizeRect.bottom += yOffset
                limitBottom()
                limitRectLB()
                limitSmallestLB()
            }

            else -> {}
        }
        invalidate()
    }

    private fun limitSmallestLB() {
        val dstRectF = RectF()
        cropMatrix.mapRect(dstRectF, sizeRect)
        if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) {
            if (dstRectF.width() < smallestEdge) {
                sizeRect.left = sizeRect.right - smallestEdge
                sizeRect.bottom = sizeRect.top + (sizeRect.width() / aspectRatio)
            }
        } else if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_FREE) {
            if (dstRectF.width() < smallestEdge) {
                sizeRect.left = sizeRect.right - smallestEdge
            }
            if (dstRectF.height() < smallestEdge) {
                sizeRect.bottom = sizeRect.top + smallestEdge
            }
        }
    }

    private fun limitSmallestRT() {
        val dstRectF = RectF()
        cropMatrix.mapRect(dstRectF, sizeRect)
        if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) {
            if (dstRectF.width() < smallestEdge) {
                sizeRect.right = sizeRect.left + smallestEdge
                sizeRect.top = sizeRect.bottom - (sizeRect.width() / aspectRatio)
            }
        } else if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_FREE) {
            if (dstRectF.width() < smallestEdge) {
                sizeRect.right = sizeRect.left + smallestEdge
            }
            if (dstRectF.height() < smallestEdge) {
                sizeRect.top = sizeRect.bottom - smallestEdge
            }
        }
    }

    private fun limitSmallestLT() {
        val dstRectF = RectF()
        cropMatrix.mapRect(dstRectF, sizeRect)
        if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) {
            if (dstRectF.width() < smallestEdge) {
                sizeRect.left = sizeRect.right - smallestEdge
                sizeRect.top = sizeRect.bottom - (sizeRect.width() / aspectRatio)
            }
        } else if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_FREE) {
            if (dstRectF.width() < smallestEdge) {
                sizeRect.left = sizeRect.right - smallestEdge
            }
            if (dstRectF.height() < smallestEdge) {
                sizeRect.top = sizeRect.bottom - smallestEdge
            }
        }

    }

    private fun limitSmallestRB() {
        val dstRectF = RectF()
        cropMatrix.mapRect(dstRectF, sizeRect)
        if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) {
            if (dstRectF.width() < smallestEdge) {
                sizeRect.right = sizeRect.left + smallestEdge
                sizeRect.bottom = sizeRect.top + (sizeRect.width() / aspectRatio)
            }
        } else if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_FREE) {
            if (dstRectF.width() < smallestEdge) {
                sizeRect.right = sizeRect.left + smallestEdge
            }
            if (dstRectF.height() < smallestEdge) {
                sizeRect.bottom = sizeRect.top + smallestEdge
            }
        }
    }


    private fun limitRectRT() {
        val dstRectF = RectF()
        cropMatrix.mapRect(dstRectF, sizeRect)
        if (dstRectF.right > limitRect.right || dstRectF.top < limitRect.top) { // 越界
            val tOver = dstRectF.top - limitRect.top
            val rOver = dstRectF.right - limitRect.right
            Log.i(TAG, "limitRectLB:  越界信息 tOver=$tOver, rOver=$rOver")
            if (tOver < 0) {
                sizeRect.top -= tOver
                if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) sizeRect.right += tOver * aspectRatio
            }
            if (rOver > 0) {
                sizeRect.right -= rOver
                if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) sizeRect.top += rOver / aspectRatio
            }
        }
    }

    private fun limitRectLT() {
        val dstRectF = RectF()
        cropMatrix.mapRect(dstRectF, sizeRect)
        if (dstRectF.left < limitRect.left || dstRectF.top < limitRect.top) { // 越界
            val tOver = dstRectF.top - limitRect.top
            val lOver = dstRectF.left - limitRect.left
            Log.i(TAG, "limitRectLB:  越界信息 tOver=$tOver, lOver=$lOver")
            if (tOver < 0) {
                sizeRect.top -= tOver
                if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) sizeRect.left -= tOver * aspectRatio
            }
            if (lOver < 0) {
                sizeRect.left -= lOver
                if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) sizeRect.top -= lOver / aspectRatio
            }
        }
    }

    private fun limitRectRB() {
        val dstRectF = RectF()
        cropMatrix.mapRect(dstRectF, sizeRect)
        if (dstRectF.right > limitRect.right || dstRectF.bottom > limitRect.bottom) { // 越界
            val bOver = dstRectF.bottom - limitRect.bottom
            val rOver = dstRectF.right - limitRect.right
            if (bOver > 0) {
                sizeRect.bottom -= bOver
                if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) sizeRect.right -= bOver * aspectRatio
            }
            if (rOver > 0) {
                sizeRect.right -= rOver
                if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) sizeRect.bottom -= rOver / aspectRatio
            }
        }
    }

    private fun limitRectLB() {
        val dstRectF = RectF()
        cropMatrix.mapRect(dstRectF, sizeRect)
        if (dstRectF.left < limitRect.left || dstRectF.bottom > limitRect.bottom) { // 越界
            val bOver = dstRectF.bottom - limitRect.bottom
            val lOver = dstRectF.left - limitRect.left
            Log.i(TAG, "limitRectLB:  越界信息 lOver: $lOver, bOver: $bOver")
            if (bOver > 0) {
                sizeRect.bottom -= bOver
                if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) sizeRect.left += bOver * aspectRatio
            }
            if (lOver < 0) {
                sizeRect.left -= lOver
                if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) sizeRect.bottom += lOver / aspectRatio
            }
        }
    }

    private fun limitBottom() {
        if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) {
            sizeRect.bottom = sizeRect.top + (sizeRect.width() / aspectRatio)
        }
    }

    private fun limitTop() {
        if (dragDropMode == CropSelectionBoxFrameLayout.DRAG_DROP_MODE_RATIO) {
            sizeRect.top = sizeRect.bottom - (sizeRect.width() / aspectRatio)
        }
    }

    fun clearCropMatrix() {
        cropMatrix.reset()
        invalidate()
    }

    /**
     * 使用反矩阵推理点击的位置是否在Box中
     */
    fun isTouchBox(x: Float, y: Float): Boolean {
        val touchPoint = inferenceClick(x, y)
        return sizeRect.contains(touchPoint[0], touchPoint[1])
    }

    /**
     *
     * 1--------2
     * |        |
     * |        |
     * |        |
     * |        |
     * 4--------3
     *
     */
    fun isTouchCorner(x: Float, y: Float): Int {

        val inferenceRect = inferenceRect(sizeRect)
        return when {
            x in ((inferenceRect.left - cornerTouchSize)..(inferenceRect.left + cornerTouchSize)) && y in ((inferenceRect.top - cornerTouchSize)..(inferenceRect.top + cornerTouchSize)) -> {
                1
            }

            x in ((inferenceRect.right - cornerTouchSize)..(inferenceRect.right + cornerTouchSize)) && y in ((inferenceRect.top - cornerTouchSize)..(inferenceRect.top + cornerTouchSize)) -> {
                2
            }

            x in ((inferenceRect.right - cornerTouchSize)..(inferenceRect.right + cornerTouchSize)) && y in ((inferenceRect.bottom - cornerTouchSize)..(inferenceRect.bottom + cornerTouchSize)) -> {
                3
            }

            x in ((inferenceRect.left - cornerTouchSize)..(inferenceRect.left + cornerTouchSize)) && y in ((inferenceRect.bottom - cornerTouchSize)..(inferenceRect.bottom + cornerTouchSize)) -> {
                4
            }

            else -> {
                -1
            }
        }
    }

    private fun inferenceClick(x: Float, y: Float): FloatArray {
        val invertedMatrix = Matrix()
        cropMatrix.invert(invertedMatrix)
        val touchPoint = floatArrayOf(x, y)
        invertedMatrix.mapPoints(touchPoint)
        return touchPoint
    }

    private fun inferenceRect(rect: RectF): RectF {
        val rectF = RectF()
        cropMatrix.mapRect(rectF, rect)
        return rectF
    }

    private var cropRect = Rect()

    /**
     *   获取sizeRect裁剪框相对于 limitRect 的位置和尺寸
     */
    fun getCropRect(): Rect {
        val rectPoints = floatArrayOf(sizeRect.left, sizeRect.top, sizeRect.right, sizeRect.bottom)
        val copyCropMatrix = Matrix().also {
            it.setValues(cropMatrix.values())
        }
        copyCropMatrix.preTranslate((-limitRect.left).toFloat(), (-limitRect.top).toFloat())
        copyCropMatrix.mapPoints(rectPoints)
        cropRect = Rect(rectPoints[0].toInt(), rectPoints[1].toInt(), rectPoints[2].toInt(), rectPoints[3].toInt())
        invalidate()
        return cropRect
    }

    private fun dp2px(value: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().displayMetrics)
    }

}
