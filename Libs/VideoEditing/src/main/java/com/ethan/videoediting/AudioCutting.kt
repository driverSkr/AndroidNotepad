package com.ethan.videoediting

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AudioCutting {

    suspend fun cropAudio(inputPath: String, outputPath: String, startTime: String, endTime: String) = suspendCoroutine { cont ->
        try {
            // 确保输出目录存在
            File(outputPath).parentFile?.mkdirs()

            // 自动保持原始格式
            val outputExt = inputPath.substringAfterLast('.', "").lowercase()
            val actualOutputPath = if (outputPath.endsWith(outputExt, true)) {
                outputPath
            } else {
                outputPath.substringBeforeLast('.') + ".$outputExt"
            }

            val command = "-y " +
                    "-i \"${inputPath}\" " +
                    "-ss $startTime " +
                    "-to $endTime " +
                    "-c copy " +
                    "-map 0:a " +
                    "\"${actualOutputPath}\""

            FFmpegKit.executeAsync(command) { session ->
                if (ReturnCode.isSuccess(session.returnCode)) {
                    cont.resume(outputPath)
                    Log.d("ethan", "音频裁剪成功！")
                } else {
                    // 如果直接复制失败，尝试重新编码
                    fallbackReencoding(inputPath, actualOutputPath, startTime, endTime, cont)
                }
            }
        } catch (e: Exception) {
            cont.resume("")
            Log.e("ethan", "执行异常: ${e.message}")
        }
    }

    /**
     * 对于无法裁剪的格式，统一转成mp3格式
     */
    private fun fallbackReencoding(
        inputPath: String,
        outputPath: String,
        startTime: String,
        endTime: String,
        cont: Continuation<String>
    ) {
        val commonPath = outputPath.substringBeforeLast('.') + ".mp3"
        val command =
            "-y " +
                    "-i \"${inputPath}\" " +
                    "-ss $startTime " +
                    "-to $endTime " +
                    "-c:a libmp3lame " +  // 使用 MP3 编码回退
                    "-q:a 2 " +            // 质量参数（0-9，0 最高）
                    "-map 0:a " +
                    "\"${commonPath}\""

        FFmpegKit.executeAsync(command) { session ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                cont.resume(commonPath)
                Log.d("ethan", "音频重新编码裁剪成功！")
            } else {
                cont.resume("")
                Log.e("ethan", "最终失败原因: ${session.allLogsAsString}")
            }
        }
    }

    /** 将 PCM 编码为 M4A */
    suspend fun encodePcmToM4a(pcmFile: File?, outputFile: File) = suspendCoroutine {suspendCoroutine ->
        if (pcmFile == null || !pcmFile.exists()) {
            suspendCoroutine.resume("failed")
        } else {
            // 确保输出目录存在
            outputFile.parentFile?.mkdirs()

            CoroutineScope(Dispatchers.Default).launch {
                try {
                    // 将命令数组转换为单个字符串
                    val command = "-y -f s16le -ar 44100 -ac 1 -i \"${pcmFile.path}\" " +
                            "-c:a aac -b:a 128k \"${outputFile.path}\""

                    FFmpegKit.executeAsync(command) { session ->
                        if (ReturnCode.isSuccess(session.returnCode)) {
                            suspendCoroutine.resume("success")
                            Log.d("AudioRecorder", "Encode to M4A success: ${outputFile.path}")
                        } else {
                            suspendCoroutine.resume("failed")
                            Log.e("AudioRecorder", "FFmpeg encode failed: ${session.allLogsAsString}")
                        }
                    }
                } catch (e: Exception) {
                    suspendCoroutine.resume("failed")
                    Log.e("AudioRecorder", "Encoding error: ${e.message}")
                }
            }
        }
    }
}