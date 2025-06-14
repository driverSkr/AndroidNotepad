package com.ethan.videoediting

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author  PengHaiChen
 * @date    2023-06-30 10:11:16
 * @email   penghaichen@tenorshare.cn
 * 视频裁切
 */
class VideoCutting {
    data class MsTimeScope(val from: Long, val to: Long)
    data class ProportionScope(val from: Double, val to: Double)

    private var inputPath: String? = null
    private var outputPath: String? = null
    private var msTimeScope: MsTimeScope? = null
    private var proportionScope: ProportionScope? = null
    private var successCallback = {}
    private var failCallback = {}

    class Builder {

        private var videoCutting = VideoCutting()

        fun videoPath(path: String): Builder {
            videoCutting.inputPath = path
            return this
        }

        /**
         * 输出视频的路径
         */
        fun outOutVideoPath(path: String): Builder {
            videoCutting.outputPath = path
            return this

        }

        /**
         * 成功回调
         */
        fun successCallBack(callback: () -> Unit): Builder {
            videoCutting.successCallback = callback
            return this
        }

        /**
         * 失败回调
         */
        fun failCallBack(callback: () -> Unit): Builder {
            videoCutting.failCallback = callback
            return this
        }

        /**
         * 按时间裁切
         */
        fun setTime(msTimeScope: MsTimeScope): Builder {
            videoCutting.proportionScope = null
            videoCutting.msTimeScope = msTimeScope
            return this
        }

        /**
         * 按百分比裁切
         */
        fun setProportion(proportionScope: ProportionScope): Builder {
            videoCutting.msTimeScope = null
            videoCutting.proportionScope = proportionScope
            return this
        }

        fun build(): VideoCutting {
            return videoCutting
        }
    }

    /**
     *
     */
    fun executionVideo() {
        val videoInfo = inputPath?.let { FfmpegVE.getVideoInfo(it) } ?: return
        if (msTimeScope == null && proportionScope == null) {
            return
        }
        CoroutineScope(Dispatchers.Default).launch {
            val command = if (msTimeScope != null) {
                val startTimeMs = msTimeScope!!.from
                val endTimeMs = msTimeScope!!.to
                val startTimeSec = startTimeMs / 1000F
                val endTimeSec = endTimeMs / 1000F
                "-i '$inputPath' -ss $startTimeSec -to $endTimeSec -c copy $outputPath"
            } else {
                val startTimeSec = videoInfo.videoDuration.times(proportionScope!!.from)
                val endTimeSec = videoInfo.videoDuration.times(proportionScope!!.to)
                "-i '$inputPath' -ss $startTimeSec -to $endTimeSec -c copy $outputPath"
            }
            FFmpegKit.executeAsync(command) { executeResponse ->
                if (ReturnCode.isSuccess(executeResponse.returnCode)) {
                    Log.i("TAG", "成功")
                    successCallback.invoke()
                } else {
                    Log.e(
                        "TAG",
                        "Command failed with state " + executeResponse.state + " and rc " + executeResponse.returnCode + ". " + executeResponse.failStackTrace
                    )
                    failCallback.invoke()
                }
            }

        }
    }

}