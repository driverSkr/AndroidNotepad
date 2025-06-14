package com.ethan.videoediting

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback
import com.arthenica.ffmpegkit.LogCallback
import com.arthenica.ffmpegkit.SessionState
import com.arthenica.ffmpegkit.StatisticsCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


open class VideoInstallBuilder : LifecycleEventObserver {
    var successCallBack: ((videoPath: String) -> Unit) = { }
    var progressCallBack: ((progress: Float) -> Unit) = { }
    var failCallBack: (() -> Unit) = { }
    var videoPath: String = ""
    var vttPath: String = ""
    var output: String = ""
    var fontPath: String = ""
    var ss: Long = 0L
    var context:Context?= null
    var exPath: String = ""
    suspend fun videoInstall() = suspendCoroutine { coroutineContext ->
        ss = 0L
        val completeCallback = FFmpegSessionCompleteCallback {
            when (it.state) {
                SessionState.FAILED -> {
                    failCallBack.invoke()
                    coroutineContext.resume(false)
                    return@FFmpegSessionCompleteCallback
                }

                SessionState.COMPLETED -> {
                    successCallBack.invoke(output)
                    coroutineContext.resume(true)
                    return@FFmpegSessionCompleteCallback
                }

                else -> {
                }
            }
        }
        val logCallback = LogCallback {
            Log.i("data", "cut: ${it.message}")
        }
        val statisticsCallback = StatisticsCallback {
//            progressCallBack.invoke(((it.time / allTime.toFloat()).toFloat()))
        }
        val subtitleFilter = String.format("subtitles=%s:force_style='Fontname= MyFontName,Fontsize=16,PrimaryColour=&HFFFFFF,OutlineColour=&H000000,BorderStyle=3,BackColour=&H80000000,Outline=1,Shadow=0,WrapStyle=1'",vttPath)
        val session = FFmpegKit.executeWithArgumentsAsync(listOf("-i", videoPath, "-vf", subtitleFilter,"-c:v","libx264","-c:a","copy", output).toTypedArray(), completeCallback, logCallback, statisticsCallback)
        Log.i("VideoInstallBuilder", "cut: ${session?.command}")
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {}
            else -> {}
        }
    }

    suspend fun init(block: VideoInstallBuilder.() -> Unit): VideoInstallBuilder {
        withContext(Dispatchers.Default) {
            block()
        }
        context?.let {
            registerApplicationFonts()
//            FFmpegKitConfig.setFontconfigConfigurationPath()
        }
        return this
    }

    @Throws(IOException::class)
     fun registerApplicationFonts() {
        // SAVE FONTS
         val fontNameMapping = HashMap<String, String>()
        fontNameMapping["MyFontName"] = "Doppio One"
        FFmpegKitConfig.setFontDirectoryList(this.context, listOf(fontPath, "/system/fonts"), fontNameMapping)
        FFmpegKitConfig.setEnvironmentVariable("FFREPORT", String.format("file=%s", File(File("${exPath}/font"), "ffreport.txt").absolutePath))
    }

}