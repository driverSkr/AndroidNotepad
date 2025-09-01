package com.ethan.android.notepad.ui.media.audio.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.blankj.utilcode.util.SizeUtils
import com.ethan.android.notepad.R
import java.lang.Math.abs
import java.lang.Math.max
import java.lang.Math.min
import java.text.DecimalFormat
import kotlin.jvm.java
import kotlin.let
import kotlin.math.roundToInt
import kotlin.ranges.coerceAtLeast
import kotlin.ranges.coerceAtMost
import kotlin.text.toDouble

class RangeSeekBar : View {
    private var absoluteMinValuePrim = 0.0
    private var absoluteMaxValuePrim = 0.0
    private var normalizedMinValue = 0.0 //点坐标占总长度的比例值，范围从0-1
    private var normalizedMaxValue = 1.0 //点坐标占总长度的比例值，范围从0-1
    private var mMincutTime: Long = 5000
    private var normalizedMinValueTime = 0.0
    private var normalizedMaxValueTime = 1.0 // normalized：规格化的--点坐标占总长度的比例值，范围从0-1
    private var mScaledTouchSlop = 0
    private var mBitmapBlack: Bitmap? = null
    private var mBitmapPro: Bitmap? = null
    private var paint: Paint? = null
    private var rectPaint: Paint? = null
    private var mGrayPaint: Paint? = null
    private var mShadowPaint: Paint? = null
    private var thumbWidth = 0
    private var thumbHeight = 0
    private var thumbHalfWidth = 0f
    private val padding = 0f
    private var mtopbottomborderheight = 0//上下两边边框高度
    private var mleftrightgraywidth = 0//左右两边灰色线条宽度
    private var mleftrightgrayheight = 0 //左右两边灰色线条高度
    private var mleftrightgraymargin = 0 //左右两边灰色线条距离顶部的间距
    private val thumbPaddingTop = 0f
    private var isTouchDown = false
    private var mActivePointerId = INVALID_POINTER_ID
    private var mDownMotionX = 0f
    private var mIsDragging = false
    private var pressedThumb: Thumb? = null
    private var isMin = false
    private var minwidth = 1.0 //最小裁剪距离
    private var notifyWhileDragging = false

    private var mLeft: Bitmap? = null
    private var mRight: Bitmap? = null

    enum class Thumb {
        MIN, MAX
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, absoluteMinValuePrim: Long, absoluteMaxValuePrim: Long) : super(context) {
        this.absoluteMinValuePrim = absoluteMinValuePrim.toDouble()
        this.absoluteMaxValuePrim = absoluteMaxValuePrim.toDouble()
        isFocusable = true
        isFocusableInTouchMode = true
        init()
    }
    fun setMinMax(absoluteMinValuePrim: Long, absoluteMaxValuePrim: Long)
    {
        this.absoluteMinValuePrim = absoluteMinValuePrim.toDouble()
        this.absoluteMaxValuePrim = absoluteMaxValuePrim.toDouble()
    }

    private fun init() {
        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        thumbWidth = SizeUtils.dp2px(20f)
        thumbHeight = SizeUtils.dp2px(44f)
        thumbHalfWidth = (thumbWidth / 2).toFloat()
        mleftrightgraywidth = SizeUtils.dp2px(2F)
        mleftrightgrayheight = SizeUtils.dp2px(20F)
        mleftrightgraymargin = SizeUtils.dp2px(21F)
        mtopbottomborderheight = SizeUtils.dp2px(3F)
        mBitmapBlack = BitmapFactory.decodeResource(resources, R.drawable.video_overlay_black)
        mBitmapPro = BitmapFactory.decodeResource(resources, R.drawable.video_overlay_trans)
        mLeft = BitmapFactory.decodeResource(resources, R.mipmap.video_cut_barleft)
        mLeft = Bitmap.createScaledBitmap(mLeft!!,SizeUtils.dp2px(20f),SizeUtils.dp2px(44f),false)

        mRight = BitmapFactory.decodeResource(resources, R.mipmap.video_cut_barright)
        mRight = Bitmap.createScaledBitmap(mRight!!,SizeUtils.dp2px(20f),SizeUtils.dp2px(44f),false)
        //绘制黑色遮罩
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //绘制白色边框
        rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rectPaint!!.style = Paint.Style.FILL
        rectPaint!!.color = context.getColor(R.color.white)
        //绘制左右两边灰色线条
        mGrayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mGrayPaint!!.color = context.getColor(R.color._990066FF)
        //绘制半透明遮罩
        mShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mShadowPaint!!.color =  context.getColor(R.color._80000000)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 300
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec) + 1
        }
        var height = 120
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = MeasureSpec.getSize(heightMeasureSpec)
        }
        setMeasuredDimension(width, height)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bgmiddleleft = 0f
        val bgmiddleright = (width - paddingRight).toFloat()
        val scale = (bgmiddleright - bgmiddleleft) / mBitmapPro!!.width
        val rangeL = normalizedToScreen(normalizedMinValue)
        val rangeR = normalizedToScreen(normalizedMaxValue)
        val scalepro = (rangeR - rangeL) / mBitmapPro!!.width
        if (scalepro > 0) {
            try {
                val promx = Matrix()
                promx.postScale(scalepro, 1f)
                val mbitmappronew = Bitmap.createBitmap(mBitmapPro!!, 0, 0, mBitmapPro!!.width,
                    mBitmapPro!!.height, promx, true)

                //画中间的透明遮罩
                canvas.drawBitmap(mbitmappronew, rangeL, thumbPaddingTop, paint)
                val mx = Matrix()
                mx.postScale(scale, 1f)
                val mbitmapblacknew = Bitmap.createBitmap(mBitmapBlack!!, 0, 0, mBitmapBlack!!.width, mBitmapBlack!!.height, mx, true)

                val cutwidthL = (rangeL - bgmiddleleft).toInt()
                val widthL = if (cutwidthL>0) cutwidthL else 1
                //画左边的半透明遮罩
                val mbgnew1 = Bitmap.createBitmap(mbitmapblacknew, 0, 0, widthL, mBitmapBlack!!.height)
                canvas.drawBitmap(mbgnew1, bgmiddleleft, thumbPaddingTop, paint)

                //画右边的半透明遮罩
                val cutwidthR = (width - rangeR).toInt()
                val widthR = if (cutwidthR>0) cutwidthR else 1
                val mbgnew2 = Bitmap.createBitmap(mbitmapblacknew, 0, 0, widthR, mBitmapBlack!!.height)
                canvas.drawBitmap(mbgnew2, (rangeR).toInt().toFloat(), thumbPaddingTop, paint)

                //画上下的矩形
                canvas.drawRect(rangeL+ thumbWidth / 2, thumbPaddingTop, rangeR- thumbWidth / 2, thumbPaddingTop + SizeUtils.dp2px(2F), rectPaint!!)
                canvas.drawRect(rangeL+ thumbWidth / 2, (height - SizeUtils.dp2px(2F)).toFloat(), rangeR- thumbWidth / 2, height.toFloat(), rectPaint!!)

//                //画左右thumb
                mLeft?.let {
                    canvas.drawBitmap(it,normalizedToScreen(normalizedMinValue),
                        0f, rectPaint!!)
                }
                mRight?.let {
                    canvas.drawBitmap(it,normalizedToScreen(normalizedMaxValue) - thumbWidth,
                        0f, rectPaint!!)
                }

            } catch (e: Exception) {
                // 当pro_scale非常小，例如width=12，Height=48，pro_scale=0.01979065时，
                // 宽高按比例计算后值为0.237、0.949，系统强转为int型后宽就变成0了。就出现非法参数异常
                Log.e(TAG,
                    "IllegalArgumentException--width=" + mBitmapPro!!.width + "Height=" + mBitmapPro!!.height
                            + "scale_pro=" + scalepro, e)
            }
        }
        invalidate()
    }


//    private fun drawThumb(screenCoord: Float, pressed: Boolean, canvas: Canvas, isLeft: Boolean) {
////        canvas.drawBitmap(pressed ? thumbPressedImage : (isLeft ? thumbImageLeft : thumbImageRight), screenCoord - (isLeft ? 0 : thumbWidth), (pressed ? thumbPressPaddingTop : thumbPaddingTop), paint);
//    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isTouchDown) {
            return super.onTouchEvent(event)
        }
        if (event.pointerCount > 1) {
            return super.onTouchEvent(event)
        }
        if (!isEnabled) return false
        if (absoluteMaxValuePrim <= mMincutTime) {

            return super.onTouchEvent(event)
        }
        val pointerIndex: Int // 记录点击点的index
        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                //记住最后一个手指点击屏幕的点的坐标x，mDownMotionX
                mActivePointerId = event.getPointerId(event.pointerCount - 1)
                pointerIndex = event.findPointerIndex(mActivePointerId)
                mDownMotionX = event.getX(pointerIndex)
                // 判断touch到的是最大值thumb还是最小值thumb
                pressedThumb = evalPressedThumb(mDownMotionX)
                if (pressedThumb == null) return super.onTouchEvent(event)
                isPressed = true // 设置该控件被按下了
                onStartTrackingTouch() // 置mIsDragging为true，开始追踪touch事件
                trackTouchEvent(event)
                attemptClaimDrag()
                if (listener != null) {
                    listener!!.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue(), MotionEvent.ACTION_DOWN, isMin, pressedThumb)
                }
            }
            MotionEvent.ACTION_MOVE -> if (pressedThumb != null) {
                if (mIsDragging) {
                    trackTouchEvent(event)
                } else {
                    pointerIndex = event.findPointerIndex(mActivePointerId)
                    val x = event.getX(pointerIndex) // 手指在控件上点的X坐标
                    // 手指没有点在最大最小值上，并且在控件上有滑动事件
                    if (kotlin.math.abs(x - mDownMotionX) > mScaledTouchSlop) {
                        isPressed = true
                        Log.e(TAG, "没有拖住最大最小值") // 一直不会执行？

                        onStartTrackingTouch()
                        trackTouchEvent(event)
                        attemptClaimDrag()
                    }
                }
                invalidate()
                if (notifyWhileDragging && listener != null) {
                    listener!!.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue(), MotionEvent.ACTION_MOVE, isMin, pressedThumb)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                } else {
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                invalidate()
                if (listener != null) {
                    listener!!.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue(), MotionEvent.ACTION_UP, isMin, pressedThumb)
                }
                pressedThumb = null // 手指抬起，则置被touch到的thumb为空
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.pointerCount - 1
                // final int index = ev.getActionIndex();
                mDownMotionX = event.getX(index)
                mActivePointerId = event.getPointerId(index)
                invalidate()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(event)
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                if (mIsDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                }
                invalidate() // see above explanation
            }
            else -> {}
        }
        return true
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.action and ACTION_POINTER_INDEX_MASK shr ACTION_POINTER_INDEX_SHIFT
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mDownMotionX = ev.getX(newPointerIndex)
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    private fun trackTouchEvent(event: MotionEvent) {
        if (event.pointerCount > 1) return
        Log.e(TAG, "trackTouchEvent: " + event.action + " x: " + event.x)
        val pointerIndex = event.findPointerIndex(mActivePointerId) // 得到按下点的index
        val x: Float = try {
            event.getX(pointerIndex)
        } catch (e: Exception) {
            return
        }
        if (Thumb.MIN == pressedThumb) {
            // screenToNormalized(x)-->得到规格化的0-1的值
            setNormalizedMinValue(screenToNormalized(x, 0))
        } else if (Thumb.MAX == pressedThumb) {
            setNormalizedMaxValue(screenToNormalized(x, 1))
        }
    }

    private fun screenToNormalized(screenCoord: Float, position: Int): Double {
        val width = width
        return if (width <= 2 * padding) {
            0.0
        } else {
            isMin = false
            var currentWidth = screenCoord.toDouble()
            val rangeL = normalizedToScreen(normalizedMinValue)
            val rangeR = normalizedToScreen(normalizedMaxValue)
            val min = mMincutTime / (absoluteMaxValuePrim - absoluteMinValuePrim) * (width - thumbWidth * 2)
            minwidth = if (absoluteMaxValuePrim > 5 * 60 * 1000) { //大于5分钟的精确小数四
                val df = DecimalFormat("0.0000")
                df.format(min).toDouble()
            } else {
                (min + 0.5).roundToInt().toDouble()
            }
            if (position == 0) {
                if (isInThumbRangeLeft(screenCoord, normalizedMinValue)) {
                    return normalizedMinValue
                }
                val rightPosition: Float = if (getWidth() - rangeR >= 0) getWidth() - rangeR else 0F
                val leftLength = getValueLength() - (rightPosition + minwidth)
                if (currentWidth > rangeL) {
                    currentWidth = rangeL + (currentWidth - rangeL)
                } else if (currentWidth <= rangeL) {
                    currentWidth = rangeL - (rangeL - currentWidth)
                }
                if (currentWidth > leftLength) {
                    isMin = true
                    currentWidth = leftLength
                }
                if (currentWidth < thumbWidth * 2 / 3) {
                    currentWidth = 0.0
                }
                val resultTime = (currentWidth - padding) / (width -  thumbWidth)
                normalizedMinValueTime = 1.0.coerceAtMost(0.0.coerceAtLeast(resultTime))
                val result = (currentWidth - padding) / (width - 2 * padding)
                1.0.coerceAtMost(0.0.coerceAtLeast(result)) // 保证该该值为0-1之间，但是什么时候这个判断有用呢？
            } else {
                if (isInThumbRange(screenCoord, normalizedMaxValue, 0.5)) {
                    return normalizedMaxValue
                }
                val rightLength = getValueLength() - (rangeL + minwidth)
                if (currentWidth > rangeR) {
                    currentWidth = rangeR + (currentWidth - rangeR)
                } else if (currentWidth <= rangeR) {
                    currentWidth = rangeR - (rangeR - currentWidth)
                }
                var paddingRight = getWidth() - currentWidth
                if (paddingRight > rightLength) {
                    isMin = true
                    currentWidth = getWidth() - rightLength
                    paddingRight = rightLength
                }
                if (paddingRight < thumbWidth * 2 / 3) {
                    currentWidth = getWidth().toDouble()
                    paddingRight = 0.0
                }
                var resultTime = (paddingRight - padding) / (width -  thumbWidth)
                resultTime = 1 - resultTime
                normalizedMaxValueTime = 1.0.coerceAtMost(0.0.coerceAtLeast(resultTime))
                val result = (currentWidth - padding) / (width - 2 * padding)
                1.0.coerceAtMost(0.0.coerceAtLeast(result)) // 保证该该值为0-1之间，但是什么时候这个判断有用呢？
            }
        }
    }

    private fun getValueLength(): Int {
        return width -  thumbWidth
    }

    /**
     * 计算位于哪个Thumb内
     *
     * @param touchX touchX
     * @return 被touch的是空还是最大值或最小值
     */
    private fun evalPressedThumb(touchX: Float): Thumb? {
        var result: Thumb? = null
        val minThumbPressed = isInThumbRange(touchX, normalizedMinValue, 2.0) // 触摸点是否在最小值图片范围内
        val maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue, 2.0)
        if (minThumbPressed && maxThumbPressed) {
            // 如果两个thumbs重叠在一起，无法判断拖动哪个，做以下处理
            // 触摸点在屏幕右侧，则判断为touch到了最小值thumb，反之判断为touch到了最大值thumb
            result = if (touchX / width > 0.5f) Thumb.MIN else Thumb.MAX
        } else if (minThumbPressed) {
            result = Thumb.MIN
        } else if (maxThumbPressed) {
            result = Thumb.MAX
        }
        return result
    }

    private fun isInThumbRange(touchX: Float, normalizedThumbValue: Double, scale: Double): Boolean {
        // 当前触摸点X坐标-最小值图片中心点在屏幕的X坐标之差<=最小点图片的宽度的一般
        // 即判断触摸点是否在以最小值图片中心为原点，宽度一半为半径的圆内。
        return kotlin.math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth * scale
    }

    private fun isInThumbRangeLeft(touchX: Float, normalizedThumbValue: Double): Boolean {
        // 当前触摸点X坐标-最小值图片中心点在屏幕的X坐标之差<=最小点图片的宽度的一般
        // 即判断触摸点是否在以最小值图片中心为原点，宽度一半为半径的圆内。
        return abs(touchX - normalizedToScreen(normalizedThumbValue) - thumbWidth) <= thumbHalfWidth * 0.5f
    }

    /**
     * 试图告诉父view不要拦截子控件的drag
     */
    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    private fun onStartTrackingTouch() {
        mIsDragging = true
    }

    private fun onStopTrackingTouch() {
        mIsDragging = false
    }
    fun getMinPostion(): Float {
       return normalizedToScreen(normalizedMinValue)
    }

    private fun normalizedToScreen(normalizedCoord: Double): Float {
        return (paddingLeft + normalizedCoord * (width - paddingLeft - paddingRight)).toFloat()
    }

    private fun valueToNormalized(value: Long): Double {
        return if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            0.0
        } else (value - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim)
    }

    fun setSelectedMinValue(value: Long) {
        if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            setNormalizedMinValue(0.0)
        } else {
            setNormalizedMinValue(valueToNormalized(value))
            normalizedMinValueTime = 0.0
        }
    }

    fun setSelectedMaxValue(value: Long) {
        if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            setNormalizedMaxValue(1.0)
        } else {
            setNormalizedMaxValue(valueToNormalized(value))
            normalizedMaxValueTime = 1.0
        }
    }

    private fun setNormalizedMinValue(value: Double) {
        normalizedMinValue = max(0.0, min(1.0, min(value, normalizedMaxValue)))

        invalidate()
    }

    private fun setNormalizedMaxValue(value: Double) {
        normalizedMaxValue = max(0.0, min(1.0, max(value, normalizedMinValue)))
        invalidate()
    }

    fun getSelectedMinValue(): Long {
        return normalizedToValue(normalizedMinValueTime)
    }

    fun getSelectedMaxValue(): Long {
        return normalizedToValue(normalizedMaxValueTime)
    }

    private fun normalizedToValue(normalized: Double): Long {
        return (absoluteMinValuePrim + normalized
                * (absoluteMaxValuePrim - absoluteMinValuePrim)).toLong()
    }


    fun setNotifyWhileDragging(flag: Boolean) {
        notifyWhileDragging = flag
    }

//    fun setTouchDown(touchDown: Boolean) {
//        isTouchDown = touchDown
//    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("SUPER", super.onSaveInstanceState())
        bundle.putDouble("MIN", normalizedMinValue)
        bundle.putDouble("MAX", normalizedMaxValue)
        bundle.putDouble("MIN_TIME", normalizedMinValueTime)
        bundle.putDouble("MAX_TIME", normalizedMaxValueTime)
        return bundle
    }

    @Suppress("DEPRECATION")
    override fun onRestoreInstanceState(parcel: Parcelable) {
        val bundle = parcel as Bundle
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"))
        normalizedMinValue = bundle.getDouble("MIN")
        normalizedMaxValue = bundle.getDouble("MAX")
        normalizedMinValueTime = bundle.getDouble("MIN_TIME")
        normalizedMaxValueTime = bundle.getDouble("MAX_TIME")
    }

    fun setMinuteTime(min_cut_time: Long) {
        this.mMincutTime = min_cut_time
    }

    private var listener: OnRangeSeekBarChangeListener? = null

    interface OnRangeSeekBarChangeListener {
        fun onRangeSeekBarValuesChanged(bar: RangeSeekBar?, minValue: Long, maxValue: Long, action: Int,
                                        isMin: Boolean, pressedThumb: Thumb?)
        fun onRangeSeekBarValuesMin()

    }

    fun setOnRangeSeekBarChangeListener(listener: OnRangeSeekBarChangeListener?) {
        this.listener = listener
    }

    companion object {
        private val TAG = RangeSeekBar::class.java.simpleName
        const val INVALID_POINTER_ID = 255
        const val ACTION_POINTER_INDEX_MASK = 0x0000ff00
        const val ACTION_POINTER_INDEX_SHIFT = 8
    }
}
