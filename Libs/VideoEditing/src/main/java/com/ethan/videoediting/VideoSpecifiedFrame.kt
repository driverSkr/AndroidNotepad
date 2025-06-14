package com.ethan.videoediting

import android.util.Log
import androidx.annotation.FloatRange
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt

private const val TAG = "VideoSpecifiedFrame"

/**
 * @author  PengHaiChen
 * @date    2023-06-28 10:15:13
 * @email   penghaichen@tenorshare.cn
 * 获得指定帧
 */
class VideoSpecifiedFrame {
    private var inputPath: String? = null
    private var outputPath: String? = null
    private var msTime: Long? = null
    private var proportion: Double? = null
    private var successCallback = {}
    private var failCallback = {}

    class Builder {

        private var videoSpecifiedFrame = VideoSpecifiedFrame()

        fun videoPath(path: String): Builder {
            videoSpecifiedFrame.inputPath = path
            return this
        }

        /**
         * 输出图片的路径
         */
        fun outOutImagePath(path: String): Builder {
            videoSpecifiedFrame.outputPath = path
            return this

        }

        /**
         * 成功回调
         */
        fun successCallBack(callback: () -> Unit): Builder {
            videoSpecifiedFrame.successCallback = callback
            return this
        }

        /**
         * 按时间获得帧,如果超过视频时长会获得最后帧,如果小于0会获得第一帧
         */
        fun setTime(msTime: Long): Builder {
            videoSpecifiedFrame.proportion = null
            videoSpecifiedFrame.msTime = msTime
            return this
        }

        /**
         * 按照视频比例获得帧
         */
        fun setProportion(@FloatRange(from = 0.0, to = 1.0) proportion: Double): Builder {
            videoSpecifiedFrame.msTime = null
            videoSpecifiedFrame.proportion = proportion
            return this
        }

        /**
         * 失败回调
         */
        fun failCallBack(callback: () -> Unit): Builder {
            videoSpecifiedFrame.failCallback = callback
            return this
        }

        fun build(): VideoSpecifiedFrame {
            return videoSpecifiedFrame
        }
    }

    /**
     * 获得指定时间的那一帧
     */
    fun executionImage() {
        val videoInfo = inputPath?.let { FfmpegVE.getVideoInfo(it) } ?: return
        if (msTime == null && proportion == null) {
            return
        }
        CoroutineScope(Dispatchers.Default).launch {
            var timingAdvance = 0L
            for (i in 0..60) { // 60次定位基本能拿到帧
                val command = if (msTime != null) { // 需要计算最后一帧的I帧位置,防止ss过去找不到帧
                    "-ss ${(msTime!! - timingAdvance) / 1000F} -i '$inputPath' -vframes 1 -q:v 2 $outputPath"
                } else {
                    val duration = videoInfo.videoDuration
                    val time = (duration * proportion!!) - (timingAdvance / 1000.0)
                    "-ss $time -i '$inputPath' -vframes 1 -q:v 2 $outputPath"
                }
                val execute = FFmpegKit.execute(command)
                if (ReturnCode.isSuccess(execute.returnCode)) {
                    outputPath?.let { // 如果存在才成功,最后一帧尝试获得60帧,肯定能拿到I帧
                        if (File(it).exists()) {
                            successCallback.invoke()
                            return@launch
                        } else {
                            timingAdvance += i * (videoInfo.videoFramesRate).roundToInt()
                        }
                    }
                } else {
                    Log.e("TAG", "Command failed with state " + execute.state + " and rc " + execute.returnCode + ". " + execute.failStackTrace)
                    failCallback.invoke()
                    return@launch
                }
            }
        }
    }

}