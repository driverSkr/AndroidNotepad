package com.ethan.videoediting

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback
import com.arthenica.ffmpegkit.LogCallback
import com.arthenica.ffmpegkit.SessionState
import com.arthenica.ffmpegkit.StatisticsCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "AiRemoveVideoCutTimeBuilder"


class AiRemoveVideoCutTimeBuilder : LifecycleEventObserver {

    var videoPath: String = ""
    var videoOutParentPath: String = ""
    var successCallBack: ((videoPath: String) -> Unit) = { }
    var progressCallBack: ((progress: Float) -> Unit) = { }

    var rtation: Int? = null
    /**
     * 开始节点,单位毫秒
     */
    var ss: Long = 0

    var to: Long = 0


    private var session: FFmpegSession? = null

    private val secPath by lazy { UUID.randomUUID() }

    private var mVideoInfo: VideoInfoBean? = null
    val videoInfo: VideoInfoBean?
        get() {
            return mVideoInfo
        }

    suspend fun cut() = suspendCoroutine { coroutineContext ->
        val path = videoOutParentPath.plus("/${secPath}")
        File(path).mkdirs()
        val resultPath = "${path}/${UUID.randomUUID()}.mp4"
        val videoinfo = FfmpegVE.getVideoInfo(videoPath)
        val completeCallback = FFmpegSessionCompleteCallback {
            when (it.state) {
                SessionState.FAILED -> {
                    coroutineContext.resume(false)
                    return@FFmpegSessionCompleteCallback
                }

                SessionState.COMPLETED -> {
                    successCallBack.invoke(resultPath)
                    coroutineContext.resume(true)
                    return@FFmpegSessionCompleteCallback
                }

                else -> {
                }
            }
        }
        val logCallback = LogCallback {
            Log.i(TAG, "cut: ${it.message}")
        }
        val statisticsCallback = StatisticsCallback {
            val allTime = to - ss
            progressCallBack.invoke(((it.time / allTime.toFloat()).toFloat()))
        }
        cancel()

        var width = if (videoinfo?.videoCodedWidth!! % 2 == 0) {
            videoinfo.videoCodedWidth
        } else {
            if (videoinfo.videoCodedWidth > 1) {
                videoinfo.videoCodedWidth - 1
            } else {
                2
            }

        }
        var height = if (videoinfo.videoCodedHeight % 2 == 0) {
            videoinfo.videoCodedHeight
        } else {
            if (videoinfo.videoCodedHeight > 1) {
                videoinfo.videoCodedHeight - 1
            } else {
                2
            }

        }
        if (rtation == 90 || rtation == 270) {
            width = if (videoinfo.videoCodedHeight % 2 == 0) {
                videoinfo.videoCodedHeight
            } else {
                if (videoinfo.videoCodedHeight > 1) {
                    videoinfo.videoCodedHeight - 1
                } else {
                    2
                }

            }
            height = if (videoinfo.videoCodedWidth % 2 == 0) {
                videoinfo.videoCodedWidth
            } else {
                if (videoinfo.videoCodedWidth > 1) {
                    videoinfo.videoCodedWidth - 1
                } else {
                    2
                }

            }
        }

        // session = FFmpegKit.executeWithArgumentsAsync(listOf("-y", "-ss", VideoEditingUtils.getTimeByMs(ss), "-i", videoPath, "-to", VideoEditingUtils.getTimeByMs(to), "-c", "copy", "-copyts", resultPath).toTypedArray(), completeCallback, logCallback, statisticsCallback)
        session =
            FFmpegKit.executeWithArgumentsAsync(mutableListOf<String>().apply {
                add("-y")
                add("-ss")
                add(VideoEditingUtils.getTimeByMs(ss))
                add("-i")
                add(videoPath)
                add("-t")
                add((VideoEditingUtils.getTimeByMs(to - ss)))

                if (videoinfo?.videoCodecName!="h264"|| videoinfo.audioCodecName !="aac")
                {

                    add("-c:v")
                    add("libx264")
                    add("-c:a")
                    add("aac")
                    add("-b:v")
                    add("2M")
                    add("-pix_fmt")
                    add("yuv420p")
                    add("-vf")
                    add("scale=$width:$height")
                }
                else
                {
                    add("-metadata:s:v")
                    add("rotate=0")
                    add("-c")
                    add("copy")
                    add("-copyts")
                }

                add(resultPath)
            }.toTypedArray(), completeCallback, logCallback, statisticsCallback)
        Log.i(TAG, "VideoCutTimeBuilder: ${session?.command}")
    }


    fun cancel() {
        session?.cancel()
        session?.sessionId?.let { FFmpegKit.cancel(it) }
    }

    suspend fun init(block: AiRemoveVideoCutTimeBuilder.() -> Unit): AiRemoveVideoCutTimeBuilder {
        withContext(Dispatchers.Main) {
            block()
        }
        withContext(Dispatchers.IO) {
            mVideoInfo = FfmpegVE.getVideoInfo(videoPath)
            to = ((mVideoInfo?.videoDuration ?: 0F) * 1000).toLong()
        }
        return this
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> session?.cancel()
            else -> {}
        }
    }

}