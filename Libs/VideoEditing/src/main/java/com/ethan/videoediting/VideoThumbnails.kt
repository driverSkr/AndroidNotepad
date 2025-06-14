package com.ethan.videoediting

import android.os.Build
import android.os.FileObserver
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File


private const val TAG = "VideoThumbnails"

/**
 * @author  PengHaiChen
 * @date    2023/6/27 16:48
 * @email   penghaichen@tenorshare.cn
 * 生成略缩图
 */
class VideoThumbnails : LifecycleEventObserver {
    private var inputPath: String? = null
    private var outputPath: String? = null
    private var interval: Int = 1000
    private var quality: Int = 100
    private var successCallback = {}
    private var gotReadyThumbnailsCallBack: ((path: String?) -> Unit)? = null
    private var failCallback = {}

    class Builder {

        private var videoThumbnails = VideoThumbnails()

        fun videoPath(path: String): Builder {
            videoThumbnails.inputPath = path
            return this
        }

        /**
         * 间隔多少时间获得略缩图,单位毫秒
         */
        fun intervals(ms: Int): Builder {
            videoThumbnails.interval = ms
            return this
        }

        /**
         * 输出图片的路径
         */
        fun outOutImagePath(path: String): Builder {
            videoThumbnails.outputPath = path
            return this
        }


        /**
         * 输出图片的质量,质量值不应大于视频最小边长
         */
        fun thumbnailQuality(quality: Int): Builder {
            videoThumbnails.quality = quality
            return this
        }

        /**
         * 成功回调
         */
        fun successCallBack(callback: () -> Unit): Builder {
            videoThumbnails.successCallback = callback
            return this
        }

        /**
         * 图片准备好的回调
         */
        fun gotReadyThumbnailsCallBack(callback: ((path: String?) -> Unit)): Builder {
            videoThumbnails.gotReadyThumbnailsCallBack = callback
            return this
        }

        /**
         * 失败回调
         */
        fun failCallBack(callback: () -> Unit): Builder {
            videoThumbnails.failCallback = callback
            return this
        }

        fun build(): VideoThumbnails {
            return videoThumbnails
        }
    }

    private var session: FFmpegSession? = null

    /**
     * 开始获得视频略缩图
     * 目前只支持1s一张略缩图
     */
    fun execution() {
        val videoInfo = inputPath?.let { FfmpegVE.getVideoInfo(it) } ?: return
        if (outputPath == null) return
        val scaleFilter = if ((videoInfo.videoCodedWidth ?: 0) > videoInfo.videoCodedHeight) {
            "scale=$quality:-1"
        } else {
            "scale=-1:$quality"
        }

        // 略缩图数量
        val thumbnailsCount = if (videoInfo.videoDuration.rem(1.0) != 0.0) { // 有小数位
            videoInfo.videoDuration.toInt() + 1
        } else { // 无小数位
            videoInfo.videoDuration.toInt()
        }


        // 监听文件夹
        val outputFileObserver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            object : FileObserver(File(outputPath!!), CREATE) {
                override fun onEvent(event: Int, path: String?) {
                    Log.i(TAG, "路径更新: $path")
                    if (path != null) {
                        gotReadyThumbnailsCallBack?.invoke(File(outputPath!!, path).absolutePath)
                    } else {
                        gotReadyThumbnailsCallBack?.invoke(null)
                    }
                }
            }
        } else {
            object : FileObserver(outputPath!!, CREATE) {
                override fun onEvent(event: Int, path: String?) {
                    Log.i(TAG, "路径更新: $path")
                    if (path != null) {
                        gotReadyThumbnailsCallBack?.invoke(File(outputPath!!, path).absolutePath)
                    } else {
                        gotReadyThumbnailsCallBack?.invoke(null)
                    }
                }
            }
        }

        outputFileObserver.startWatching()

        val command = if (thumbnailsCount <= 10) {
            val d = videoInfo.videoDuration / 10
            "-i '$inputPath' -vf \"fps=1/${d},${scaleFilter}\" -vsync 0 ${outputPath}/thumbnails-%03d.png"
        } else {
            "-i '$inputPath' -vf \"fps=1,${scaleFilter}\" -vsync 0 ${outputPath}/thumbnails-%03d.png"
        }
        Log.i(TAG, "execution: Thumbnails commend  is $command")
        session = FFmpegKit.executeAsync(command) { executeResponse -> // 导出略缩图
            if (ReturnCode.isSuccess(executeResponse.returnCode)) {
                Log.i(TAG, "execution: 生成完成")
                successCallback.invoke()
                outputFileObserver.stopWatching()
            } else {
                Log.e("TAG", "Command failed with state " + executeResponse.state + " and rc " + executeResponse.returnCode + ". " + executeResponse.failStackTrace)
                failCallback.invoke()
                outputFileObserver.stopWatching()
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                session?.cancel()
            }

            else -> {}
        }
    }

}