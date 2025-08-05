package com.ethan.android.notepad.utils

import android.util.Log
import coil3.Bitmap
import coil3.size.Size
import coil3.transform.Transformation

object AsyncImageUtils {

    class FrameIndexTransformation(frameIndex: Int, string: String) : Transformation() {
        override val cacheKey = string + "FrameIndexTransformation($frameIndex)"
        private val myString = string

        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
            // 这里需要实现获取特定帧的逻辑
            // 实际实现可能需要使用更复杂的方法解码WebP帧
            Log.i("Tag", "FrameIndexTransformation$myString")
            return input // 简化示例，实际需要提取特定帧
        }
    }
}