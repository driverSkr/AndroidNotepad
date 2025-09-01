package com.ethan.android.notepad.ui.media.audio.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import androidx.annotation.AttrRes
import com.ethan.android.notepad.R

class AudioTumbScorllView : HorizontalScrollView {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var progress = 0f

    //总时长
    var mTrackView: AudioTumbListView? = null
    private var mHandler: Handler? = null
    private var scrollType = ScrollType.IDLE

    private var currentX = 0
    var mScrollViewListener: ScrollViewListener? = null



    enum class ScrollType {
        IDLE, TOUCH_SCROLL, FLING
    }

    constructor(context: Context) : super(context) {
        onInitialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        onInitialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        onInitialize(context)
    }

    private fun onInitialize(context: Context) {
        paint.color = Color.WHITE
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.all_audio_tumb_list_layout, this)
        mTrackView = findViewById(R.id.main_video_track)
        mHandler = Handler(Looper.getMainLooper())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mHandler?.post(scrollRunnable)
            }
            MotionEvent.ACTION_MOVE -> {
                progress = scrollX / (mTrackView?.measuredWidth!! - mTrackView?.paddingLeft!!).toFloat()
                this.scrollType = ScrollType.TOUCH_SCROLL
                mScrollViewListener?.onScrollChanged(scrollType)
                mHandler?.removeCallbacks(scrollRunnable)
            }
        }
        return super.onTouchEvent(event)
    }

    fun setMyPadding(value: Int) {
        mTrackView?.setPadding(value, 0, value, 0)
        mTrackView?.requestLayout()
        requestLayout()
    }

    fun setWidth(totleWidth:Int, frame:Int,videoPath:String,framecount:Int) {
        mTrackView?.setWidth(totleWidth, frame,videoPath,framecount)
        requestLayout()
    }


    private val scrollRunnable: Runnable = object : Runnable {
        override fun run() {
            progress = scrollX / (mTrackView?.measuredWidth!! - mTrackView?.paddingLeft!!).toFloat()
            if (scrollX == currentX) {
                //滚动停止,取消监听线程
                scrollType = ScrollType.IDLE
                mScrollViewListener?.onScrollChanged(scrollType)
                mHandler?.removeCallbacks(this)
                return
            } else {
                //手指离开屏幕,但是view还在滚动
                scrollType = ScrollType.FLING
                mScrollViewListener?.onScrollChanged(scrollType)
            }
            currentX = scrollX
            //滚动监听间隔:milliseconds
            mHandler?.postDelayed(this, 50)
        }
    }

    interface ScrollViewListener {
        fun onScrollChanged(scrollType: ScrollType?)
    }
}