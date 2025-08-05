//package com.ethan.android.notepad.widget
//
//import android.animation.ValueAnimator
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Matrix
//import android.graphics.Paint
//import android.graphics.Path
//import android.graphics.Rect
//import android.util.AttributeSet
//import android.view.GestureDetector
//import android.view.MotionEvent
//import android.view.ScaleGestureDetector
//import android.view.animation.Animation
//import android.view.animation.LinearInterpolator
//import androidx.appcompat.widget.AppCompatImageView
//import com.ethan.android.notepad.R
//import com.ethan.android.notepad.extension.dp
//import com.ethan.android.notepad.model.FilterGenerator
//import com.ethan.android.notepad.model.HandlerType
//import com.ethan.android.notepad.utils.DataHelper
//import kotlin.math.max
//import kotlin.math.min
//
//
//private const val TAG = "CompareView"
//
//
//open class CompareView : AppCompatImageView {
//    private lateinit var bitmapNew: Bitmap
//    private lateinit var bitmapOld: Bitmap
//    private var bitmapSlide: Bitmap
//    private var paint = Paint()
//    private var path = Path()
//    private var radius = 0f
//    private var valueAnimator = ValueAnimator()
//    private var isOffset = false
//    private var isScale = false
//    private var touchWidth = 40
//    private var offsetX = 0F
//    private var maxOldScale = 3.0f
//    private var minOldScale = 1.0f
//    private var maxNewScale = 3.0f
//    private var minNewScale = 1.0f
//    private var handlerType: HandlerType? = null
//    private var bWidth = 0f
//    private var bHeight = 0f
//    private var nWidth = 0f
//    private var nHeight = 0f
//    private var isShowBasemap = true
//    private var twelve: Float = 0f
//    private var isSecond = false  // true表示是Ai photo或Ai ari 在种子栏点击原图
//    private var isNeedRefreshLayout = false
//    private var isWorkData = false  // 是否从作品库进入
//    private var offX = 0f
//    private var offY = 0f
//
//    constructor(context: Context) : super(context)
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//
//    private var originScale = 1F
//    fun getOriginScale(): Float {
//        return originScale
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        if ((!::bitmapOld.isInitialized || !::bitmapNew.isInitialized) && !isSecond && !isNeedRefreshLayout) {
//            return
//        }
//        arrayOld.fill(0f)
//        arrayNew.fill(0f)
//        bWidth = bitmapOld.width.toFloat()
//        bHeight = bitmapOld.height.toFloat()
//        nWidth = bitmapNew.width.toFloat()
//        nHeight = bitmapNew.height.toFloat()
//        val sWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
//        val sHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat() - 25.dp
//        if ((handlerType != HandlerType.AIPhoto && handlerType != HandlerType.SD) || (handlerType == HandlerType.SD && isWorkData)) {
//            val hei = bHeight / bWidth * sWidth
//            val scale2: Float
//            val scale = if (hei <= sHeight) {
//                setMeasuredDimension(sWidth.toInt(), hei.toInt())
//                scale2 = sWidth / nWidth
//                sWidth / bWidth
//            } else {
//                setMeasuredDimension((bWidth / bHeight * sHeight).toInt(), sHeight.toInt())
//                scale2 = sHeight / nHeight
//                sHeight / bHeight
//            }
//            // 定义初始最小缩放
//            minOldScale = scale
//            maxOldScale = scale + 3f
//            matrixOld.getValues(arrayOld)
//            arrayOld[0] = scale
//            arrayOld[4] = scale
//            matrixOld.setValues(arrayOld)
//            minNewScale = scale2
//            maxNewScale = maxOldScale / minOldScale * scale2
//            matrixNew.getValues(arrayNew)
//            arrayNew[0] = scale2
//            arrayNew[4] = scale2
//            matrixNew.setValues(arrayNew)
//            offsetX = measuredWidth.toFloat() / 2
//            originScale = scale2
//            isNeedRefreshLayout = false
//        } else { // 获取缩放
//            if (!isSecond && !isNeedRefreshLayout) {
//                val hei = nHeight / nWidth * sWidth
//                val scale: Float = if (hei <= sHeight) {
//                    setMeasuredDimension(sWidth.toInt(), hei.toInt())
//                    sWidth / nWidth
//                } else {
//                    setMeasuredDimension((nWidth / nHeight * sHeight).toInt(), sHeight.toInt())
//                    sHeight / nHeight
//                } // 定义初始最小缩放
//                minOldScale = scale
//                maxOldScale = scale + 3f
//                minNewScale = scale
//                maxNewScale = maxOldScale / minOldScale * scale
//                matrixNew.getValues(arrayNew)
//                offX = (measuredWidth - nWidth * scale) / 2
//                offY = (measuredHeight - nHeight * scale) / 2
//                arrayNew[0] = scale  // x倍数
//                arrayNew[4] = scale  // y倍数
//                arrayNew[2] = offX // x位移
//                arrayNew[5] = offY // y位移
//                matrixNew.setValues(arrayNew)
//                offsetX = measuredWidth.toFloat() / 2
//                originScale = scale
//            } else {
//                isNeedRefreshLayout = false
//                val hei = bHeight / bWidth * sWidth
//                val scale: Float = if (hei <= sHeight) {
//                    setMeasuredDimension(sWidth.toInt(), hei.toInt())
//                    sWidth / bWidth
//                } else {
//                    setMeasuredDimension((bWidth / bHeight * sHeight).toInt(), sHeight.toInt())
//                    sHeight / bHeight
//                } // 定义初始最小缩放
//                minOldScale = scale
//                maxOldScale = scale + 3f
//                matrixOld.getValues(arrayOld)
//                offX = (measuredWidth - bWidth * scale) / 2
//                offY = (measuredHeight - bHeight * scale) / 2
//                arrayOld[0] = scale  // x倍数
//                arrayOld[4] = scale  // y倍数
//                arrayOld[2] = offX // x位移
//                arrayOld[5] = offY // y位移
//                matrixOld.setValues(arrayOld)
//                offsetX = measuredWidth.toFloat() / 2
//                originScale = scale
//            }
//        }
//
//    }
//
//    init {
//        paint.strokeWidth = 5f
//        paint.isAntiAlias = true
//        paint.color = Color.WHITE
//        bitmapSlide = BitmapFactory.decodeResource(context.resources, R.mipmap.compareview_tubm_icon)
//        touchWidth = bitmapSlide.width
//        twelve = context.resources.getDimension(R.dimen.dp_12)
//        apply {
//            scaleDetector = ScaleGestureDetector(context, ScaleDetectorListener())
//            scrollDetector = GestureDetector(context, ScrollDetectorListener())
//        }
//    }
//
//    fun isSecond(isSecond: Boolean) {
//        this.isSecond = isSecond
//        requestLayout()
//        // initScale(bitmapOld.width.toFloat(), bitmapOld.height.toFloat())
//        invalidate()
//    }
//
//    fun setRequestLayout(isNeedRefreshLayout: Boolean) {
//        this.isNeedRefreshLayout = isNeedRefreshLayout
//    }
//
//    fun setSrc(newBitmap: Bitmap, oldBitmap: Bitmap, radius: Float, handlerType: HandlerType? = null, isWorkData: Boolean? = false) {
//        bitmapNew = newBitmap
//        bitmapOld = oldBitmap
//        this.handlerType = handlerType
//        this.isWorkData = isWorkData!!
//        if (isNeedRefreshLayout) {
//            requestLayout()
//        }
//        if (handlerType == HandlerType.AIPhoto) {
//            this.isShowBasemap = false
//            if (isSecond) {
//                isNeedRefreshLayout = true
//                requestLayout()
//            }
//        }
//        if (handlerType == HandlerType.SD) {
//            this.isShowBasemap = false
//            if (!isWorkData) {
//                if (isSecond) {
//                    requestLayout()
//                }
//            }
//        }
//        if (handlerType == HandlerType.AI_BABY){
//            //不对比
//            this.isShowBasemap = false
//        }
//        isSecond = false
//        this.radius = radius
//        displayTutorial()
//    }
//
//    private fun displayTutorial() {
//        if (!DataHelper.isShowBeginnerSGuideContrastLineAnimation(context)) {
//            postDelayed({
//                autoRunLine()
//            }, 200)
//        } else {
//            invalidate()
//        }
//    }
//
//    @SuppressLint("DrawAllocation")
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        path.reset()
//        checkBound() // SD卡通化不显示原图
//        if (isShowBasemap) {
//            canvas.save() // 绘制底图
//            canvas.drawBitmap(bitmapOld, matrixOld, paint)
//            canvas.restore()
//            canvas.save() // 绘制展示图
//            canvas.clipRect(offsetX, 0f, width.toFloat(), height.toFloat())
//            canvas.drawColor(Color.BLACK)
//            canvas.drawBitmap(bitmapNew, matrixNew, paint)
//            canvas.restore() // 绘制线
//            canvas.drawBitmap(bitmapSlide, offsetX - bitmapSlide.width / 2, height.toFloat() * 3 / 5, paint)
//            canvas.drawLine(offsetX, 0f, offsetX, height.toFloat(), paint)
//        } else {
//            if (isSecond) {
//                canvas.drawBitmap(bitmapOld, matrixOld, paint)
//            } else {
//                canvas.drawBitmap(bitmapNew, matrixNew, paint)
//            }
//        }
//        if (radius == 0f) {
//            return
//        } // 绘制圆角
//        path.moveTo(0f, 0f)
//        path.lineTo(radius, 0f)
//        path.arcTo(0f, 0f, radius * 2, radius * 2, -90f, -90f, false)
//        path.close()
//        canvas.drawPath(path, paint)
//        path.moveTo(width.toFloat() - radius, 0f)
//        path.lineTo(width.toFloat(), 0f)
//        path.lineTo(width.toFloat(), radius)
//        path.arcTo(width.toFloat() - radius * 2, 0f, width.toFloat(), radius * 2, 0f, -90f, false)
//        path.close()
//        canvas.drawPath(path, paint)
//        path.moveTo(width.toFloat(), height.toFloat() - radius)
//        path.lineTo(width.toFloat(), height.toFloat())
//        path.lineTo(width.toFloat() - radius, height.toFloat())
//        path.arcTo(width.toFloat() - radius * 2, height.toFloat() - radius * 2, width.toFloat(), height.toFloat(), 90f, -90f, false)
//        path.close()
//        canvas.drawPath(path, paint)
//        path.moveTo(radius, height.toFloat())
//        path.lineTo(0f, height.toFloat())
//        path.lineTo(0f, height.toFloat() - radius)
//        path.arcTo(0f, height.toFloat() - radius * 2, radius * 2, height.toFloat(), -180f, -90f, false)
//        path.close()
//        canvas.drawPath(path, paint)
//    }
//
//    private var lastX = 0f
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> { // 解决与Scrollview滑动冲突
//                parent.requestDisallowInterceptTouchEvent(true)
//                valueAnimator.pause()
//                isOffset = false
//                isScale = false
//                lastX = event.x
//                if (lastX in (offsetX - touchWidth / 2)..(offsetX + touchWidth / 2)) {
//                    isOffset = true
//                }
//                if (!isShowBasemap) {
//                    invalidate()
//                }
//            }
//
//            MotionEvent.ACTION_MOVE -> {
//                val dx = event.x - lastX
//                if (isOffset) {
//                    offsetX += dx
//                    if (offsetX < 0) {
//                        offsetX = 0f
//                    }
//                    if (offsetX > (width)) {
//                        offsetX = (width).toFloat()
//                    }
//                } else {
//                    parent.requestDisallowInterceptTouchEvent(false)
//                }
//                lastX = event.x
//            }
//
//            MotionEvent.ACTION_UP -> {
//                isOffset = false
//                isScale = false
//            }
//        }
//        if (!isOffset && event != null) {
//            scaleDetector?.onTouchEvent(event)
//            scrollDetector?.onTouchEvent(event)
//        }
//        callback?.onOffset(offsetX)
//        invalidate()
//        return true
//    }
//
//    // 添加自动扫描
//    @SuppressLint("WrongConstant")
//    private fun autoRunLine() {
//        valueAnimator.setFloatValues(width.toFloat() / 2, width.toFloat() * 3 / 8, width.toFloat() * 5 / 8, width.toFloat() / 2)
//        valueAnimator.duration = 1000
//        valueAnimator.repeatCount = 1
//        valueAnimator.repeatMode = Animation.RESTART
//        valueAnimator.interpolator = LinearInterpolator()
//        valueAnimator.addUpdateListener {
//            offsetX = it.animatedValue as Float
//            callback?.onOffset(offsetX)
//            invalidate()
//        }
//        valueAnimator.start()
//    }
//
//    private var scaleDetector: ScaleGestureDetector? = null
//    private var scrollDetector: GestureDetector? = null
//
//    private val matrixOld = Matrix()
//    private val matrixNew = Matrix()
//    private val arrayOld = FloatArray(9)
//    private val arrayNew = FloatArray(9)
//    private fun checkBound() {
//        if ((handlerType != HandlerType.AIPhoto && handlerType != HandlerType.SD) || (handlerType == HandlerType.SD && isWorkData)) {
//            matrixOld.getValues(arrayOld) // 2 X平移  5 Y平移
//            arrayOld[2] = min(0f, arrayOld[2])
//            arrayOld[2] = max(bWidth * minOldScale - bWidth * arrayOld[0], arrayOld[2])
//            arrayOld[5] = min(0f, arrayOld[5])
//            arrayOld[5] = max(bHeight * minOldScale - bHeight * arrayOld[0], arrayOld[5])
//            matrixOld.setValues(arrayOld)
//            matrixNew.getValues(arrayNew)
//            arrayNew[2] = arrayOld[2]
//            arrayNew[5] = arrayOld[5]
//            matrixNew.setValues(arrayNew)
//        } else {
//            matrixOld.getValues(arrayOld) // 2 X平移  5 Y平移\
//            arrayOld[2] = min(0f, arrayOld[2])
//            arrayOld[2] = max(bWidth * minOldScale - bWidth * arrayOld[0], arrayOld[2])
//            arrayOld[5] = min(0f, arrayOld[5])
//            arrayOld[5] = max(bHeight * minOldScale - bHeight * arrayOld[0], arrayOld[5])
//            matrixOld.setValues(arrayOld)
//            matrixNew.getValues(arrayNew)
//            arrayNew[2] = min(0f, arrayNew[2])
//            arrayNew[2] = max(bWidth * minOldScale - bWidth * arrayNew[0], arrayNew[2])
//            arrayNew[5] = min(0f, arrayNew[5])
//            arrayNew[5] = max(bHeight * minOldScale - bHeight * arrayNew[0], arrayNew[5])
//            matrixNew.setValues(arrayNew)
//        }
//    }
//
//    inner class ScrollDetectorListener : GestureDetector.SimpleOnGestureListener() {
//        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
//            if (!isScale) {
//                matrixOld.getValues(arrayOld)
//                matrixOld.preTranslate(-distanceX / arrayOld[0], -distanceY / arrayOld[0])
//                matrixNew.getValues(arrayNew)
//                matrixNew.preTranslate(-distanceX / arrayNew[0], -distanceY / arrayNew[0])
//            }
//            return true
//        }
//    }
//
//    inner class ScaleDetectorListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//
//        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
//            isScale = true
//            return super.onScaleBegin(detector)
//        }
//
//        override fun onScaleEnd(detector: ScaleGestureDetector) {
//            isScale = false
//        }
//
//        override fun onScale(detector: ScaleGestureDetector): Boolean {
//            detector.apply {
//                if ((handlerType != HandlerType.AIPhoto && handlerType != HandlerType.SD) || (isWorkData && handlerType == HandlerType.SD)) {
//                    matrixOld.preScale(scaleFactor, scaleFactor, focusX, focusY)
//                    matrixOld.getValues(arrayOld)
//                    if (arrayOld[0] > maxOldScale) {
//                        matrixOld.preScale(maxOldScale / arrayOld[0], maxOldScale / arrayOld[0], focusX, focusY)
//                    }
//                    if (arrayOld[0] < minOldScale) {
//                        matrixOld.preScale(minOldScale / arrayOld[0], minOldScale / arrayOld[0], focusX, focusY)
//                    }
//                    matrixNew.preScale(scaleFactor, scaleFactor, focusX, focusY)
//                    matrixNew.getValues(arrayNew)
//                    if (arrayNew[0] > maxNewScale) {
//                        matrixNew.preScale(maxNewScale / arrayNew[0], maxNewScale / arrayNew[0], focusX, focusY)
//                    }
//                    if (arrayNew[0] < minNewScale) {
//                        matrixNew.preScale(minNewScale / arrayNew[0], minNewScale / arrayNew[0], focusX, focusY)
//                    }
//                } else {
//                    if (!isSecond) {
//                        offX = if (arrayNew[2] > 0) {
//                            focusX - arrayNew[2]
//                        } else {
//                            focusX
//                        }
//                        offY = if (arrayNew[5] > 0) {
//                            focusY - arrayNew[5]
//                        } else {
//                            focusY
//                        }
//                    } else {
//                        offX = if (arrayOld[2] > 0) {
//                            focusX - arrayOld[2]
//                        } else {
//                            focusX
//                        }
//                        offY = if (arrayOld[5] > 0) {
//                            focusY - arrayOld[5]
//                        } else {
//                            focusY
//                        }
//                    }
//                    matrixNew.preScale(scaleFactor, scaleFactor, offX, offY)
//                    matrixNew.getValues(arrayNew)
//                    if (arrayNew[0] > maxNewScale) {
//                        matrixNew.preScale(maxNewScale / arrayNew[0], maxNewScale / arrayNew[0], offX, offY)
//                    }
//                    if (arrayNew[0] < minNewScale) {
//                        matrixNew.preScale(minNewScale / arrayNew[0], minNewScale / arrayNew[0], offX, offY)
//                    }
//                    matrixOld.preScale(scaleFactor, scaleFactor, offX, offY)
//                    matrixOld.getValues(arrayOld)
//                    if (arrayOld[0] > maxOldScale) {
//                        matrixOld.preScale(maxOldScale / arrayOld[0], maxOldScale / arrayOld[0], offX, offY)
//                    }
//                    if (arrayOld[0] < minOldScale) {
//                        matrixOld.preScale(minOldScale / arrayOld[0], minOldScale / arrayOld[0], offX, offY)
//                    }
//                }
//            }
//            return true
//        }
//    }
//
//    private var callback: OnOffsetCallback? = null
//
//
//    interface OnOffsetCallback {
//        fun onOffset(offset: Float)
//    }
//
//    protected fun setOffX(x: Float) {
//        offsetX = x
//        invalidate()
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        if (::bitmapOld.isInitialized) {
//            bitmapOld.recycle()
//        }
//        if (::bitmapNew.isInitialized) {
//            bitmapNew.recycle()
//        }
//        bitmapNew.recycle()
//        bitmapSlide.recycle()
//        valueAnimator.cancel()
//        valueAnimator.removeAllListeners()
//        valueAnimator.removeAllUpdateListeners()
//    }
//
//    suspend fun setFilterNewBitmap(filterPath: FilterMaterialPkgDownloadViewModel.FilterPath, originNewBmpPath: String) {
//        bitmapNew = FilterGenerator.genFinisFilterImage(
//            originNewBmpPath,
//            originScale,
//            filterPath,
//            Rect(0, 0, bitmapNew.width, bitmapNew.height),
//                                                       ) ?: bitmapNew
//        invalidate()
//    }
//
//    fun currNewBitmapSize(): IntArray {
//        return intArrayOf(bitmapNew.width, bitmapNew.height)
//    }
//
//}