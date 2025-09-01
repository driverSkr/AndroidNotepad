package com.ethan.android.notepad.ui.media.audio.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.AttrRes
import com.ethan.android.notepad.common.extension.dpF
import java.lang.Math.random
import kotlin.apply
import kotlin.ranges.until
import kotlin.text.toFloat
import kotlin.text.toInt

class AudioTumbListView : ViewGroup {
    private var originWidth = 0
    private var myWidth = width
    private var frameWidth = 0
    private val paint = Paint().apply {
        color = Color.WHITE
    }

    //主轴的layout

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = myWidth
        val height = MeasureSpec.getSize(heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        originWidth = width + paddingLeft + paddingRight
        setMeasuredDimension(originWidth, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val tumbwidth = 2.dpF
        val count = (myWidth.div(tumbwidth)).toInt()

        canvas.save()
        for(i in 0 until count) {
            if (i%2==1)
            {
                val halfh = height/2 -4.dpF
                val hv = random()*halfh
                canvas.drawRoundRect((i*tumbwidth).toFloat()+paddingLeft,hv.toFloat(), ((i+1)*tumbwidth).toFloat()+paddingLeft,(height-hv).toFloat(),4.dpF,4.dpF,paint)
            }
        }
        canvas.restore()
    }

    fun setWidth(width: Int, frameW: Int, videoPath: String, framecount: Int) {
        myWidth = width
        frameWidth = frameW
        requestLayout()
    }



}