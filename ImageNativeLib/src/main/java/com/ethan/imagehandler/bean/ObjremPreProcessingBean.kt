package com.ethan.imagehandler.bean

import android.graphics.Bitmap
import androidx.annotation.Keep

/**
 * @author  PengHaiChen
 * @date    2024/5/29 9:16
 * @email   penghaichen@tenorshare.cn
 */
@Keep
class ObjremPreProcessingBean(
    var img: Bitmap,
    var mask: Bitmap,
    var x: Int,
    var y: Int,
    var w: Int,
    var h: Int,

    var xMask: Int,
    var yMask: Int,
    var wMask: Int,
    var hMask: Int,
                             ) {
    override fun toString(): String {
        return "ObjremPreProcessingBean(img=$img, mask=$mask, x=$x, y=$y, w=$w, h=$h, xMask=$xMask, yMask=$yMask, wMask=$wMask, hMask=$hMask)"
    }
}
