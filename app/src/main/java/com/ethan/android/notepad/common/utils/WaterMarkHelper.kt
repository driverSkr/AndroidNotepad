package com.ethan.android.notepad.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.ethan.android.notepad.common.utils.BitmapUtils.dp2px
import com.ethan.android.notepad.common.utils.BitmapUtils.getScreenHeight
import com.ethan.android.notepad.common.utils.BitmapUtils.getScreenWidth
import com.ethan.android.notepad.common.model.WatermarkPosition
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object WaterMarkHelper {

    /**
     * 图片加水印 - Canvas绘制方式
     */
    fun addImageWatermark(context: Context, src: Bitmap, watermark: Bitmap): Bitmap? {
        val width = dp2px(context, 40f)
        val height = dp2px(context, 40f)
        val right = dp2px(context, 17f)
        val bottom = dp2px(context, 12f)
        val viewMaxWidth = getScreenWidth(context)
        val viewMaxHeight = getScreenHeight(context) - dp2px(context, 162f)
        val ret = src.config?.let { src.copy(it, true) }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.alpha = 255
        val rectSrc = Rect(0, 0, watermark.width, watermark.height)
        val drawWidth: Int
        val drawHeight: Int
        val drawRight: Int
        val drawBottom: Int
        val scale = if (src.width >= src.height) viewMaxWidth.toFloat() / src.width.toFloat() else viewMaxHeight.toFloat() / src.height.toFloat()
        drawWidth = (width.toFloat() / scale).toInt()
        drawHeight = (height.toFloat() / scale).toInt()
        drawRight = (right.toFloat() / scale).toInt()
        drawBottom = (bottom.toFloat() / scale).toInt()

        val x = Integer.max(0, src.width - drawWidth - drawRight)
        val y = Integer.max(0, src.height - drawHeight - drawBottom)
        val rectDest = Rect(x, y, x + drawWidth, y + drawHeight)
        ret?.let { Canvas(it) }?.drawBitmap(watermark, rectSrc, rectDest, paint)
        return ret
    }

    /**
     * 图片添加水印 - ffmpeg方式
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

            FFmpegKit.executeAsync(command) { session ->
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
}