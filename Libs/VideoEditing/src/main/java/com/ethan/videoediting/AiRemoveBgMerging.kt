package com.ethan.videoediting

import androidx.annotation.WorkerThread
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AiRemoveBgMerging {
    lateinit var resPath: String
    lateinit var resMaskPath: String
    lateinit var outPutFilePath: String
    var videow: Int = 1
    var videoh: Int = 1
    @WorkerThread
    suspend fun doMerging(): String = withContext(Dispatchers.IO) {
        // 验证输入文件
        val inputFiles = listOf(resPath, resMaskPath)
        if (!inputFiles.all { File(it).exists() }) {
            return@withContext ""
        }


        // 构建 filter_complex，根据 isIpInFront 调整叠加顺序
        val (firstVideo, firstMask)=listOf(resPath, resMaskPath)


        val filterComplex = """
                [0:v]scale=$videow:$videoh[v1];
                [1:v]scale=$videow:$videoh[m1];
                [v1][m1]alphamerge[outv]
            """.trimIndent()

        val command = arrayListOf(
            "-i", firstVideo,
            "-i", firstMask,
            "-filter_complex", filterComplex,
            "-map","[outv]",
            "-c:v", "libvpx-vp9", // 使用支持透明通道的编码器
            "-pix_fmt", "yuva420p", // 使用支持 alpha 通道的像素格式
            "-auto-alt-ref", "0",
            "-crf", "30",
            "-b:v", "0",
            "-y",
            outPutFilePath.replace(".mp4", ".webm") // 使用支持透明通道的容器格式
        )

        val session = FFmpegKit.execute(command.joinToString(" "))
        val returnCode = session.returnCode

        if (ReturnCode.isSuccess(returnCode)) {
            return@withContext outPutFilePath.replace(".mp4", ".webm")
        } else {
            return@withContext ""
        }

    }
}