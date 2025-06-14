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
import java.io.File
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "AiRemoveBgConvert"

class AiRemoveBgConvert : LifecycleEventObserver {

    var videoPath: String = ""
    var videoOutParentPath: String = ""
    var successCallBack: ((videoPath: String) -> Unit) = { }
    var failCallBack: (() -> Unit) = { }
    var progressCallBack: ((progress: Float) -> Unit) = { }
    var myVideoinfo: VideoInfoBean? = null
    var rtation: Int? = null

    private var session: FFmpegSession? = null

    private val secPath by lazy { UUID.randomUUID() }

    suspend fun convert() = suspendCoroutine { coroutineContext ->
        val path = videoOutParentPath.plus("/${secPath}")
        File(path).mkdirs()
        val resultPath = "${path}/${UUID.randomUUID()}.mp4"
        myVideoinfo = FfmpegVE.getVideoInfo(videoPath)
        val completeCallback = FFmpegSessionCompleteCallback {
            when (it.state) {
                SessionState.FAILED -> {

                    failCallBack.invoke()
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

        }
        cancel()
        var width = if (myVideoinfo?.videoCodedWidth!! % 2 == 0) {
            myVideoinfo?.videoCodedWidth!!
        } else {
            if (myVideoinfo?.videoCodedWidth!! > 1) {
                myVideoinfo?.videoCodedWidth!! - 1
            } else {
                2
            }

        }
        var height = if (myVideoinfo?.videoCodedHeight!! % 2 == 0) {
            myVideoinfo?.videoCodedHeight!!
        } else {
            if (myVideoinfo?.videoCodedHeight!! > 1) {
                myVideoinfo?.videoCodedHeight!! - 1
            } else {
                2
            }

        }
        if (rtation == 90 || rtation == 270) {
            width = if (myVideoinfo?.videoCodedHeight!! % 2 == 0) {
                myVideoinfo?.videoCodedHeight!!
            } else {
                if (myVideoinfo?.videoCodedHeight!! > 1) {
                    myVideoinfo?.videoCodedHeight!! - 1
                } else {
                    2
                }

            }
            height = if (myVideoinfo?.videoCodedWidth!! % 2 == 0) {
                myVideoinfo?.videoCodedWidth!!
            } else {
                if (myVideoinfo?.videoCodedWidth!! > 1) {
                    myVideoinfo?.videoCodedWidth!! - 1
                } else {
                    2
                }

            }
        }
        // session = FFmpegKit.executeWithArgumentsAsync(listOf("-y", "-ss", VideoEditingUtils.getTimeByMs(ss), "-i", videoPath, "-to", VideoEditingUtils.getTimeByMs(to), "-c", "copy", "-copyts", resultPath).toTypedArray(), completeCallback, logCallback, statisticsCallback)
        session =
            FFmpegKit.executeWithArgumentsAsync(mutableListOf<String>().apply {
                add("-i")
                add(videoPath)
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
                add(resultPath)
            }.toTypedArray(), completeCallback, logCallback, statisticsCallback)
        Log.i(TAG, "convert: ${session?.command}")
    }


    fun cancel() {
        session?.cancel()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> session?.cancel()
            else -> {}
        }
    }
}