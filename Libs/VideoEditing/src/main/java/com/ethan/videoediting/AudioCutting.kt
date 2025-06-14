package com.ethan.videoediting

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AudioCutting {

    fun cropAudio(inputPath: String, outputPath: String, startTime: String, endTime: String) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // 确保输出目录存在
                File(outputPath).parentFile?.mkdirs()

                val command = "-y -i \"$inputPath\" -ss $startTime -to $endTime -c:a libmp3lame \"$outputPath\""

                FFmpegKit.executeAsync(command) { executeResponse ->
                    if (ReturnCode.isSuccess(executeResponse.returnCode)) {
                        Log.d("ethan", "音频裁剪成功！")
                    } else {
                        Log.e("ethan", "失败原因: ${executeResponse.allLogsAsString}")
                    }
                }
            } catch (e: Exception) {
                Log.e("ethan", "执行异常: ${e.message}")
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