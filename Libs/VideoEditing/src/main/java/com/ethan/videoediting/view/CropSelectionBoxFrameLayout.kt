package com.ethan.videoediting.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnLayoutChangeListener
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import com.ethan.videoediting.VideoInfoBean

private const val TAG = "CropSelectionBoxFrameLa"

/**
 * @author  PengHaiChen
 * @date    2023/7/3 17:56
 * @email   penghaichen@tenorshare.cn
 * 用来选择视频裁切范围的布局,需要子View都不抢点击事件
 */
class CropSelectionBoxFrameLayout : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val cropSelectionBox: CropSelectionBox by lazy {
        CropSelectionBox(this.context).also {
            it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
    }

    init {
        this.doOnLayout {
            addView(cropSelectionBox)
        }
    }

    companion object {
        const val DRAG_DROP_MODE_FREE = 0x00000001
        const val DRAG_DROP_MODE_RATIO = 0x00000002
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    /**
     * 选中Box
     */
    private var isSelectBox = false

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
    private var isSelectCorner = -1

    private var touchPoint = Point()

    var dragDropMode = DRAG_DROP_MODE_RATIO
        set(value) {
            field = value
            cropSelectionBox.dragDropMode = value
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isSelectBox = cropSelectionBox.isTouchBox(event.x, event.y)
                isSelectCorner = cropSelectionBox.isTouchCorner(event.x, event.y)
                Log.i(TAG, "onTouchEvent: 点击${event.x}:${event.y}  角选中${isSelectCorner}")
                touchPoint.x = event.x.toInt()
                touchPoint.y = event.y.toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                if (isSelectBox) {
                    if (isSelectCorner == -1) { // 未选中角才允许移动
                        cropSelectionBox.moveCropBox(event.x - touchPoint.x, event.y - touchPoint.y)
                        touchPoint.x = event.x.toInt()
                        touchPoint.y = event.y.toInt()
                    }
                }
                if (isSelectCorner != -1) {
                    cropSelectionBox.moveCropCorner(event.x - touchPoint.x, event.y - touchPoint.y, isSelectCorner)
                    touchPoint.x = event.x.toInt()
                    touchPoint.y = event.y.toInt()
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                isSelectBox = false
            }
        }
        return true
    }


    /**
     *  获得裁剪器的位置,返回原视频的尺寸
     */
    fun getCropPosition(): Rect {
        val cropRect = cropSelectionBox.getCropRect()
        return Rect((cropRect.left * srcVideoScale).toInt(), (cropRect.top * srcVideoScale).toInt(), (cropRect.right * srcVideoScale).toInt(), (cropRect.bottom * srcVideoScale).toInt())
    }

    fun getHCropPosition(): Rect {
        val cropRect = cropSelectionBox.getCropRect()
        return Rect(cropRect)
    }

    private var dstVideoScale = 1F
    private var srcVideoScale = 1F
    private var limitRect = Rect()

    fun getLimitRect(): Rect {
        return Rect(limitRect)
    }

    /**
     * 尺寸选择,给定真实尺寸,自动进行比例计算后应用到View上
     */
    fun initSelectSize(width: Int, height: Int, videoInfo: VideoInfoBean) {
        // 满足尺寸要求就不显示选择框
        // if ((width.toFloat() / height.toFloat()) == (videoInfo.width!!.toFloat() / videoInfo.height!!.toFloat())) {
        //     cropSelectionBox.visibility = GONE
        //     return
        // }
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child is CropSelectDstView) {
                var layoutListener: OnLayoutChangeListener? = null
                layoutListener = OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                    limitRect = Rect(child.left, child.top, child.right, child.bottom)
                    val sourceRect = Rect(0, 0, width, height)

                    dstVideoScale = calculateScaleFactor(sourceRect, limitRect)

                    val dstW = (width * dstVideoScale).toInt()
                    val dstH = (height * dstVideoScale).toInt()
                    cropSelectionBox.setSelectSize(dstW, dstH, limitRect)

                    val videoRect = Rect(0, 0, videoInfo.videoCodedWidth,
                        videoInfo.videoCodedHeight
                    )
                    val dstRect = Rect(0, 0, dstW, dstH)
                    srcVideoScale = calculateScaleFactor(dstRect, videoRect)
                    Log.i(TAG, "setSelectSize: $limitRect , dstVideoScale $dstVideoScale srcVideoScale $srcVideoScale,dstW $dstW dstH $dstH")

                }
                child.addOnLayoutChangeListener(layoutListener)
            }
        }

    }

    fun refresh(width: Int, height: Int, videoInfo: VideoInfoBean) {
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child is CropSelectDstView) {
                limitRect = Rect(child.left, child.top, child.right, child.bottom)
                val sourceRect = Rect(0, 0, width, height)

                dstVideoScale = calculateScaleFactor(sourceRect, limitRect)

                val dstW = (width * dstVideoScale).toInt()
                val dstH = (height * dstVideoScale).toInt()
                cropSelectionBox.setSelectSize(dstW, dstH, limitRect)

                val videoRect = Rect(0, 0, videoInfo.videoCodedWidth, videoInfo.videoCodedHeight)
                val dstRect = Rect(0, 0, dstW, dstH)
                srcVideoScale = calculateScaleFactor(dstRect, videoRect)
                Log.i(TAG, "setSelectSize: $limitRect , dstVideoScale $dstVideoScale srcVideoScale $srcVideoScale,dstW $dstW dstH $dstH")
            }
        }
    }

    private fun calculateScaleFactor(srcRect: Rect, dstRect: Rect): Float {
        val srcAspectRatio = srcRect.width().toFloat() / srcRect.height().toFloat()
        val dstAspectRatio = dstRect.width().toFloat() / dstRect.height().toFloat()

        return if (srcAspectRatio > dstAspectRatio) {
            dstRect.width().toFloat() / srcRect.width().toFloat()
        } else {
            dstRect.height().toFloat() / srcRect.height().toFloat()
        }
    }

}