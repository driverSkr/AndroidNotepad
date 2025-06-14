package com.ethan.videoediting

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt


private const val TAG = "VideoLastFrame"

/**
 * @author  PengHaiChen
 * @date    2023-06-28 10:15:13
 * @email   penghaichen@tenorshare.cn
 * 生成最后一帧
 */
class VideoLastFrame {
    private var inputPath: String? = null
    private var outputPath: String? = null
    private var successCallback = {}
    private var failCallback = {}

    class Builder {

        private var videoLastFrame = VideoLastFrame()

        fun videoPath(path: String): Builder {
            videoLastFrame.inputPath = path
            return this
        }

        /**
         * 输出图片的路径
         */
        fun outOutImagePath(path: String): Builder {
            videoLastFrame.outputPath = path
            return this

        }

        /**
         * 成功回调
         */
        fun successCallBack(callback: () -> Unit): Builder {
            videoLastFrame.successCallback = callback
            return this
        }

        /**
         * 失败回调
         */
        fun failCallBack(callback: () -> Unit): Builder {
            videoLastFrame.failCallback = callback
            return this
        }

        fun build(): VideoLastFrame {
            return videoLastFrame
        }
    }

    /**
     * 开始获得视频最后一帧
     */
    fun execution() {
        var timingAdvance = 0L
        val videoInfo = inputPath?.let { FfmpegVE.getVideoInfo(it) } ?: return
        val duration = videoInfo.videoDuration
        CoroutineScope(Dispatchers.Default).launch {
            for (i in 0..60) {
                val command =
                    "-ss ${duration - (timingAdvance / 1000.0)} -i '$inputPath' -vframes 1 -q:v 2 $outputPath"
                val execute = FFmpegKit.execute(command)
                if (ReturnCode.isSuccess(execute.returnCode)) {
                    outputPath?.let {
                        if (File(it).exists()) {
                            successCallback.invoke()
                            return@launch
                        } else {
                            timingAdvance += i * videoInfo.videoFramesRate.roundToInt()
                        }
                    }
                } else {
                    Log.e(
                        "TAG",
                        "Command failed with state " + execute.state + " and rc " + execute.returnCode + ". " + execute.failStackTrace
                    )
                    failCallback.invoke()
                    return@launch
                }
            }
        }
    }
}
