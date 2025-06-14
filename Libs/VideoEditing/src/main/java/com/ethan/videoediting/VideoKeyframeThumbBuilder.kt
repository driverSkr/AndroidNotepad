package com.ethan.videoediting

import android.media.MediaMetadataRetriever
import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.SessionState
import com.blankj.utilcode.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "VideoKeyframeThumbBuilder"

/**
 * @author PengHaiChen
 * @date 2024/1/1 11:34:48
 * @description
 */
class VideoKeyframeThumbBuilder : LifecycleEventObserver {

    var videoPath: String = ""
    var thumbOutPath: String = ""
    var callBack: ((Map<Int, String>) -> Unit) = {}

    /**
     * 视频略缩图最大宽度,高等比计算,最大建议别超过500,文件夹会爆炸
     */
    @IntRange(to = 500)
    var maxWidth = 100

    /**
     * 关键帧数量
     */
    var keyframeCount = 10

    private var mVideoInfo: VideoInfoBean? = null
    val videoInfo: VideoInfoBean?
        get() {
            return mVideoInfo
        }

    /**
     * FFmpegKit.executeWithArgumentsAsync(
     * listOf(
     * "-y",
     * "-skip_frame", "nokey",
     * "-i", videoPath,
     * "-q:v", "0.5",
     * "-vf", "scale=${maxWidth}:-1",
     * "-an",
     * "-vsync", "0",
     * "-start_number", "0",
     * "${path}/%d.jpg"
     * ).toTypedArray(), completeCallback, logCallback, statisticsCallback
     */
    suspend fun genKeyFrameThumb() = suspendCoroutine<Boolean> { coroutineContext ->
        val duration = videoInfo?.videoDuration ?: return@suspendCoroutine
        // 小于10s采用更详细的略缩图,大于10s采用ss方式循环抽帧
        val  xx= (duration* videoInfo?.videoAvgFrameRate!!)/keyframeCount
//        val interval = (duration * 1000F / keyframeCount)
//
//
//                val session =  FFmpegKit.executeWithArguments(listOf("-i", videoPath, "-vf", "select=not(mod(n'\',xx))", "-vsync ","vfr","output_%03d.jpg").toTypedArray())
//
////                val session = FFmpegKit.executeWithArguments(listOf("-y", "-ss", VideoEditingUtils.getTimeByMs((i * interval).toLong()), "-i", videoPath, "-q:v", "0.5", "-frames:v", "1", "-vf", "scale=${maxWidth}:-1", path).toTypedArray())
//                if (session.state == SessionState.FAILED) {
//                    coroutineContext.resume(false)
//                    return@suspendCoroutine
//                } else if (session.state == SessionState.COMPLETED) {
//                    callBack.invoke(mutableMapOf<Int, String>().apply { put(i, path) })
//                }
//
//        coroutineContext.resume(true)
    }


    suspend fun genKeyFrameThumb(i: Int) =
        suspendCoroutine<Pair<Int, String>?> { coroutineContext ->
            val duration = videoInfo?.videoDuration ?: return@suspendCoroutine
            val interval = (duration * 1000F / keyframeCount)
            val path = "${thumbOutPath}/$i.jpg"
            if (FileUtils.isFileExists(path)) {
                coroutineContext.resume(Pair(i, path))
            } else {
                val session = FFmpegKit.executeWithArguments(listOf("-y", "-ss", VideoEditingUtils.getTimeByMs((i * interval).toLong()), "-i", videoPath, "-frames:v", "1", "-vf", "thumbnail,scale=${maxWidth}:-1", path).toTypedArray())
                if (session.state == SessionState.FAILED) {
                    coroutineContext.resume(null)
                    return@suspendCoroutine
                } else if (session.state == SessionState.COMPLETED) {
                    coroutineContext.resume(Pair(i, path))
                }
            }
        }



    suspend fun init(block: VideoKeyframeThumbBuilder.() -> Unit): VideoKeyframeThumbBuilder {
        withContext(Dispatchers.Main) {
            block()
        }
        withContext(Dispatchers.IO) {
            mVideoInfo = FfmpegVE.getVideoInfo(videoPath)

        }
        return this
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {}
            else -> {}
        }
    }

    suspend  fun getVideoThumbnail(i: Int)     =    suspendCoroutine { coroutineContext ->
        val retriever = MediaMetadataRetriever()
         try {
            // 或者获取特定时间的帧（微秒单位）
            val duration = videoInfo?.videoDuration ?: 0F
            val interval = (duration * 1000F * 1000 / keyframeCount)
            val path = "${thumbOutPath}/$i.jpg"

             retriever.setDataSource(videoPath)

//            if (FileUtils.isFileExists(path)) {
//                coroutineContext.resume(Pair(i, ImageUtils.getBitmap(path, 150, 150)))
//            } else {
                val bmp = retriever.getFrameAtTime((i*interval).toLong(), MediaMetadataRetriever.OPTION_CLOSEST)

                coroutineContext.resume(Pair(i,bmp))
//            }

        } catch (e: Exception) {
            e.printStackTrace()
            coroutineContext.resume(null)
        } finally {
            try {
                retriever.release()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}