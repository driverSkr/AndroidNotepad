package com.ethan.videoediting

import android.util.Log
import com.arthenica.ffmpegkit.MediaInformationSession

private const val TAG = "VideoInfoUtils"


internal object VideoInfoUtils {
    fun getMediaInformation(mediaInformation: MediaInformationSession?): VideoInfoBean? {
        if (mediaInformation == null) {
            Log.e(TAG, "Video parameter acquisition exception")
            return null
        }
        val videoProperties = mediaInformation.mediaInformation?.allProperties ?: return null
        Log.i(TAG, "getMediaInformation: $videoProperties")
        val streams = videoProperties.getJSONArray("streams")
        val videoInfoBean = VideoInfoBean()
        var audioTrackCount = 0
        for (i in 0 until streams.length()) {
            val stream = streams.getJSONObject(i)
            val string =stream.getString("codec_type")
            when (string) {
                "video" -> {
                    kotlin.runCatching { videoInfoBean.videoCodecName = stream.getString("codec_name") } // 解码器名字
                    videoInfoBean.videoCodedWidth = stream.getInt("coded_width") // 宽度
                    videoInfoBean.videoCodedHeight = stream.getInt("coded_height") // 高度
                    kotlin.runCatching { stream.getString("bit_rate") }.onSuccess { videoInfoBean.videoBitRate =it.toIntOrNull()?:-1 }.onFailure { videoInfoBean.videoBitRate =-1 }
                    kotlin.runCatching { stream.getString("duration") }.onSuccess { videoInfoBean.videoDuration =it.toFloatOrNull()?:-1F }.onFailure { videoInfoBean.videoDuration =-1F }
                    kotlin.runCatching { stream.getString("nb_frames") }.onSuccess { videoInfoBean.videoNbFrames =it.toIntOrNull()?:-1 }.onFailure { videoInfoBean.videoNbFrames =-1 }
                    val avgFrame = stream.getString("avg_frame_rate").split("/")
                    videoInfoBean.videoAvgFrameRate =
                        avgFrame[0].toFloat() / avgFrame[1].toFloat()// 平均帧,这个不一定准确,建议用全帧数除时间
                }

                "audio" -> {
                    kotlin.runCatching {  videoInfoBean.audioCodecName = stream.getString("codec_name") } // 解码器名字
                    audioTrackCount++
                }
            }
        }
        videoInfoBean.audioTractCount = audioTrackCount
        return videoInfoBean
    }

    fun logMediaInformation(videoInfo: VideoInfoBean?) {
        val border = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
        val head = "| "
        Log.i(TAG, border)
        Log.i(TAG, head + "Video Information LOG:")
        Log.i(TAG, border)
        Log.i(TAG, head + "Video Codec Name:              ${videoInfo?.videoCodecName}")
        Log.i(TAG, head + "Video Coded Width:             ${videoInfo?.videoCodedWidth}")
        Log.i(TAG, head + "Video Coded Height:            ${videoInfo?.videoCodedHeight}")
        Log.i(TAG, head + "Video Bit Rate:                ${videoInfo?.videoBitRate}")
        Log.i(TAG, head + "Video Duration:                ${videoInfo?.videoDuration}")
        Log.i(TAG, head + "Video Number of Frames:        ${videoInfo?.videoNbFrames}")
        Log.i(TAG, head + "Video Average Frame Rate:      ${videoInfo?.videoAvgFrameRate}")
        Log.i(TAG, head + "Audio Codec Name:              ${videoInfo?.audioCodecName}")
        Log.i(TAG, head + "Audio Track Count:             ${videoInfo?.audioTractCount}")
        Log.i(TAG, border)
    }
}