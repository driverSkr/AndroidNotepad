package com.ethan.android.notepad.ui.media.audio.view

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.common.extension.dpF
import com.ethan.android.notepad.common.utils.VideoHelper
import com.ethan.android.notepad.common.utils.formatHMSTime2
import com.ethan.android.notepad.common.utils.formatMSCTime
import com.ethan.android.notepad.common.utils.formatSecondTime
import com.ethan.android.notepad.databinding.LayoutAudioTimeLineBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@SuppressLint("ComposableNaming", "ClickableViewAccessibility")
@Composable
/** 音频裁剪时间轴 */
fun AudioTimeLine(
    inPutPath: String,
    exoPlayer: ExoPlayer,
    playState: MutableState<Boolean>,
    startTime: MutableLongState,
    endTime: MutableLongState,
    selectTime: MutableIntState,
    currentTime: MutableState<String>
) {
    val context = LocalContext.current
    val allDuration = VideoHelper.getVideoInfo(inPutPath) ?: 0L

    BackHandler { }
    AndroidView(factory = { c ->
        val binding = LayoutAudioTimeLineBinding.inflate(LayoutInflater.from(c))
        var maxLine = 60 * 1000L // 视频最多剪切多长时间
        val minLine = 3 * 1000L // 视频最多剪切多长时间
        val frameCount: Int
        val duration = if (allDuration > maxLine) maxLine else allDuration
        if (allDuration < 60 * 1000) {
            frameCount = 5
            maxLine = allDuration
        } else {
            maxLine = 60 * 1000L
            frameCount = allDuration.div(duration / 5).toInt()
        }

        val textView = TextView(context)
        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.topMargin = SizeUtils.dp2px(27f)
        layoutParams.leftMargin = SizeUtils.dp2px(20f)
        layoutParams.gravity = Gravity.START
        textView.layoutParams = layoutParams
        val mOnRangeSeekBarChangeListener: RangeSeekBar.OnRangeSeekBarChangeListener = object : RangeSeekBar.OnRangeSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onRangeSeekBarValuesChanged(
                bar: RangeSeekBar?,
                minValue: Long,
                maxValue: Long,
                action: Int,
                isMin: Boolean,
                pressedThumb: RangeSeekBar.Thumb?,
            ) {
                val start = allDuration.times(binding.tumbListView.progress).toLong()
                val leftProgress = minValue + start
                val rightProgress = maxValue + start
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        playState.value = false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        exoPlayer.seekTo(if (pressedThumb === RangeSeekBar.Thumb.MIN) leftProgress else rightProgress)
                        startTime.longValue = leftProgress
                        endTime.longValue = rightProgress

                        layoutParams.leftMargin = (SizeUtils.dp2px(20f) + (bar?.getMinPostion() ?: 0f)).toInt()
                        textView.layoutParams = layoutParams
                        textView.text = (rightProgress - leftProgress).formatHMSTime2()
                        selectTime.intValue = (rightProgress - leftProgress).formatSecondTime().toInt()
                        if ((rightProgress - leftProgress) < 3000) {
                            textView.visibility = View.GONE
                        } else {
                            textView.visibility = View.VISIBLE
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        exoPlayer.seekTo(leftProgress)
                        playState.value = true
                    }
                }
            }

            override fun onRangeSeekBarValuesMin() {

            }
        }
        val scrollX = 0
        binding.tumbListView.scrollTo(scrollX, 0)
        binding.tumbListView.progress = 0f
        exoPlayer.seekTo(0, 0)

        binding.tempSpace.post {
            binding.tumbListView.setMyPadding(binding.tempSpace.width)
            val frameWidth = (ScreenUtils.getScreenWidth() - 2 * binding.tempSpace.width).div(5)
            val totalWidth = frameWidth.times(frameCount)
            binding.tumbListView.setWidth(totalWidth, frameWidth, inPutPath, frameCount)
        }
        val mRangeSeekBar = RangeSeekBar(context, 0L, maxLine)
        mRangeSeekBar.setSelectedMaxValue(maxLine)
        mRangeSeekBar.setNotifyWhileDragging(true)
        mRangeSeekBar.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener)
        mRangeSeekBar.setMinuteTime(minLine)
        binding.timelineLayout.addView(mRangeSeekBar)

        val startInit = allDuration.times(binding.tumbListView.progress).toLong()
        val leftProgressInit = mRangeSeekBar.getSelectedMinValue().plus(startInit)
        val rightProgressInit = mRangeSeekBar.getSelectedMaxValue().plus(startInit)

        textView.setBackgroundColor(context.getColor(R.color._B3000000))
        textView.text = (rightProgressInit - leftProgressInit).formatHMSTime2()
        selectTime.intValue = (rightProgressInit - leftProgressInit).formatSecondTime().toInt()
        textView.textSize = SizeUtils.sp2px(3f).toFloat()
        textView.setTextColor(context.getColor(R.color.white))

        startTime.longValue = leftProgressInit
        endTime.longValue = rightProgressInit

        binding.timelineLayout.addView(textView)
        binding.layoutBomm.setOnTouchListener { _, motionEvent ->

            Log.i("  binding.timeLine.setOnTouchListener", "  binding.timeLine.setOnTouchListener" + motionEvent.x)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    playState.value = false
                }

                MotionEvent.ACTION_UP -> {
                    playState.value = true
                    val start = allDuration.times(binding.tumbListView.progress).toLong()
                    val timePosition = motionEvent.x.div(binding.videoEditFrameLayout.width)
                    val data = start + (maxLine.times(timePosition)).toLong()

                    exoPlayer.seekTo(data)
                }

            }
            binding.timeLine.x = motionEvent.x - 5.dpF

            return@setOnTouchListener true
        }

        binding.tumbListView.mScrollViewListener = object : AudioTumbScorllView.ScrollViewListener {
            override fun onScrollChanged(scrollType: AudioTumbScorllView.ScrollType?) {
                when (scrollType) {
                    AudioTumbScorllView.ScrollType.FLING, AudioTumbScorllView.ScrollType.TOUCH_SCROLL -> {
                        val start = allDuration.times(binding.tumbListView.progress).toLong()
                        val leftProgress = mRangeSeekBar.getSelectedMinValue().plus(start)
                        exoPlayer.seekTo(leftProgress)
                        playState.value = false
                    }

                    AudioTumbScorllView.ScrollType.IDLE -> {
                        playState.value = true
                        val nowSeekPosition = exoPlayer.currentPosition
                        val start = allDuration.times(binding.tumbListView.progress).toLong()
                        val leftProgress = mRangeSeekBar.getSelectedMinValue().plus(start)
                        val rightProgress = mRangeSeekBar.getSelectedMaxValue().plus(start)
                        exoPlayer.seekTo(leftProgress)
                        val cutTime = nowSeekPosition.minus(start)
                        val timePosition = cutTime.div(duration.toFloat())
                        val position = timePosition.times(binding.videoEditFrameLayout.width)
                        binding.timeLine.x = position - 5.dpF

                        startTime.longValue = leftProgress
                        endTime.longValue = rightProgress

                        textView.text = (rightProgress - leftProgress).formatHMSTime2()
                        selectTime.intValue = (rightProgress - leftProgress).formatSecondTime().toInt()
                    }

                    else -> {}
                }
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                // 定时任务逻辑
                delay(30) // 延迟 1 秒
                if (exoPlayer.playWhenReady) {
                    val nowSeekPosition = exoPlayer.currentPosition
                    val start = allDuration.times(binding.tumbListView.progress).toLong()
                    val leftProgress = mRangeSeekBar.getSelectedMinValue().plus(start)
                    val rightProgress = mRangeSeekBar.getSelectedMaxValue().plus(start)
                    if (nowSeekPosition >= rightProgress) {
                        exoPlayer.seekTo(leftProgress)
                    }
                    val cutTime = nowSeekPosition.minus(start)
                    val timePosition = cutTime.div(duration.toFloat())
                    val width = ScreenUtils.getScreenWidth() - 2 * binding.tempSpace.width
                    val position = timePosition.times(width)
                    binding.timeLine.x = position - 5.dpF

                    currentTime.value = (start + (maxLine.times(timePosition)).toLong()).formatMSCTime()

                }
            }
        }

        binding.root
    }, modifier = Modifier.wrapContentSize())
}