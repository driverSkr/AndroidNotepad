package com.ethan.videoediting

import com.arthenica.ffmpegkit.FFmpegKit
import java.io.File
import java.util.UUID


class VideoFrameMerging {

    lateinit var bitmapListPath: String
    lateinit var outputPath: String
    var frameRate: Int = 30
    var audioPath: String = ""

    suspend fun doComposite(): Boolean {
        val isHasAudio = audioPath.isNotBlank()

        val mergeVideosSession = FFmpegKit.executeWithArguments(listOf(
            "-framerate", "$frameRate",
            "-i", "${bitmapListPath}/%d.jpeg",
            "-vf", "scale='iw-mod(iw,2)':'ih-mod(ih,2)'", // 添加缩放参数,向下找最接近的偶数
            "-c:v", "libx264",
            "-pix_fmt", "yuv420p",
            outputPath,
                                                                      ).toTypedArray())
        if (!mergeVideosSession.returnCode.isValueSuccess) {
            return false
        }
        if (!isHasAudio) {
            return true
        }

        val parent = File(outputPath).parent
        val transPath = "${parent}/trans${UUID.randomUUID()}.${File(outputPath).extension}"
        File(outputPath).copyTo(File(transPath), true)
        File(outputPath).delete()

        val mergeAudioSession = FFmpegKit.executeWithArguments(listOf(
            "-i", transPath,
            "-i", audioPath,
            "-c:v", "copy",
            "-c:a", "aac",
            "-strict", "experimental",
            "-filter_complex", "[1:a]apad[a]",
            "-map", "0:v:0",
            "-map", "[a]",
            "-shortest",
            outputPath,
                                                                     ).toTypedArray())
        File(transPath).delete()

        return mergeAudioSession.returnCode.isValueSuccess
    }
}