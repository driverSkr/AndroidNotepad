package com.ethan.imagehandler.bean

import android.graphics.Bitmap
import androidx.annotation.Keep

/**
 * @author  PengHaiChen
 * @date    2024/5/8 20:32
 * @email   penghaichen@tenorshare.cn
 */
@Keep
class FaceBean(
    var x: Float = 0.0f,
    var y: Float = 0.0f,
    var w: Float = 0.0f,
    var h: Float = 0.0f,
    var prob: Float = 0.0f,
    var faceThumb: Bitmap? = null,
)

