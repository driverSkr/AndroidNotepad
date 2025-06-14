package com.ethan.videoediting

import com.arthenica.ffmpegkit.FFprobeKit

object FfmpegVE {

    fun getVideoInfo(videoPath: String): VideoInfoBean? {
        val currentTimeMillis = System.currentTimeMillis()
        val mediaInformation = FFprobeKit.getMediaInformation(videoPath)
        val videoInfoBean = VideoInfoUtils.getMediaInformation(mediaInformation)
        VideoInfoUtils.logMediaInformation(videoInfoBean)
        return videoInfoBean
    }

}