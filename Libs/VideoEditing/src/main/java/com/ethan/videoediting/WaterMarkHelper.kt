package com.ethan.videoediting

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.ethan.videoediting.model.WatermarkPosition
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object WaterMarkHelper {

    /**
     * 图片添加水印
     */
    suspend fun addImageWaterMark(inputVideoPath: String, outputVideoPath: String, watermarkImagePath: String, position: WatermarkPosition) = suspendCoroutine { cont ->
        try {
            val positionCommand = when (position) {
                WatermarkPosition.TOP_LEFT -> "10:10" // x:y坐标
                WatermarkPosition.TOP_RIGHT -> "main_w-overlay_w-10:10"
                WatermarkPosition.BOTTOM_LEFT -> "10:main_h-overlay_h-10"
                WatermarkPosition.BOTTOM_RIGHT -> "main_w-overlay_w-10:main_h-overlay_h-10"
                WatermarkPosition.CENTER -> "(main_w-overlay_w)/2:(main_h-overlay_h)/2"
            }

            val command = "-i $inputVideoPath -i $watermarkImagePath " +
                    "-filter_complex \"[1]format=rgba,colorchannelmixer=aa=0.7[logo];[0][logo]overlay=$positionCommand\" " +
                    "-codec:a copy -y $outputVideoPath"

            FFmpegKit.executeAsync(command.toString()) { session ->
                if (ReturnCode.isSuccess(session.returnCode)) {
                    cont.resume(true)
                    Log.d("ethan", "音频裁剪成功！")
                } else {
                    // 如果直接复制失败，尝试重新编码
                    cont.resume(false)
                }
            }
        } catch (e: Exception) {
            cont.resume(false)
            Log.e("ethan", "图片加水印执行异常: ${e.message}")
        }
    }

    /**
     * 视频添加水印（带更多配置选项）
     * @param inputVideoPath 输入视频路径
     * @param outputVideoPath 输出视频路径
     * @param watermarkVideoPath 水印视频路径
     * @param position 水印位置
     * @param opacity 水印透明度 (0.0 - 1.0)
     * @param scale 水印缩放比例 (0.0 - 1.0)
     * @param enableAudio 是否保留音频
     */
    suspend fun addVideoWaterMark(
        inputVideoPath: String,
        outputVideoPath: String,
        watermarkVideoPath: String,
        position: WatermarkPosition,
        opacity: Float = 0.7f,
        scale: Float = 1.0f,
        enableAudio: Boolean = true
    ) = suspendCoroutine { cont ->
        try {
            val positionCommand = when (position) {
                WatermarkPosition.TOP_LEFT -> "10:10"
                WatermarkPosition.TOP_RIGHT -> "main_w-overlay_w-10:10"
                WatermarkPosition.BOTTOM_LEFT -> "10:main_h-overlay_h-10"
                WatermarkPosition.BOTTOM_RIGHT -> "main_w-overlay_w-10:main_h-overlay_h-10"
                WatermarkPosition.CENTER -> "(main_w-overlay_w)/2:(main_h-overlay_h)/2"
            }

            // 构建缩放命令（如果需要缩放）
//            val scaleCommand = if (scale != 1.0f) {
//                "scale=iw*$scale:ih*$scale,"
//            } else {
//                ""
//            }
//
//            // 构建音频处理命令
//            val audioCommand = if (enableAudio) {
//                "-c:a aac -b:a 128k"
//            } else {
//                "-an" // 不保留音频
//            }
//
//            val command = "-i $inputVideoPath -i $watermarkVideoPath " +
//                    "-filter_complex \"[1]${scaleCommand}format=rgba,colorchannelmixer=aa=$opacity[watermark];[0][watermark]overlay=$positionCommand:shortest=1\" " +
//                    "-c:v libx264 -preset fast -crf 23 $audioCommand -y $outputVideoPath"


            val scaleCommand = if (scale != 1.0f) {
                "scale=iw*$scale:ih*$scale,"
            } else {
                ""
            }

            // 构建音频处理命令
            val audioCommand = if (enableAudio) {
                "-c:a aac -b:a 128k"
            } else {
                "-an" // 不保留音频
            }

            // 修正的滤镜命令 - 使用正确的像素格式和明确的流选择
            val command = "-i \"$inputVideoPath\" -i \"$watermarkVideoPath\" " +
                    "-filter_complex \"[1:v]${scaleCommand}format=yuva420p,colorchannelmixer=aa=$opacity[watermark];[0:v][watermark]overlay=$positionCommand:format=auto\" " +
                    "-c:v libx264 -preset fast -crf 23 $audioCommand -y \"$outputVideoPath\""

            Log.d("ethan", "执行FFmpeg命令: $command")

            FFmpegKit.executeAsync(command) { session ->
                if (ReturnCode.isSuccess(session.returnCode)) {
                    cont.resume(true)
                    Log.d("ethan", "视频加水印成功！输出路径: $outputVideoPath")
                } else {
                    cont.resume(false)
                    Log.e("ethan", "视频加水印失败，返回码: ${session.returnCode}, 错误: ${session.failStackTrace}")
                }
            }
        } catch (e: Exception) {
            cont.resume(false)
            Log.e("ethan", "视频加水印执行异常: ${e.message}")
        }
    }

}