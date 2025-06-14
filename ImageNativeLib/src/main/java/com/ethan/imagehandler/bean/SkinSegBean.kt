package com.ethan.imagehandler.bean

import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.PointF
import androidx.annotation.Keep

/**
 * @Author      : Spike Peng
 * @Email       : penghaichen@tenorshare.cn
 * @Date        : on 2024/11/6 16:14.
 * @Description :
 */
@Keep
class SkinSegBean(
    var maskImagePath: String,// mask路径
    var pixelID: Int,// 颜色ID
                 ) {

    var centroid: PointF? = null// 中心点
    var maskPath: Path? = null// mask的边缘路径
    var maskAlphaBitmap: Bitmap? = null// mask本体alpha

    companion object {
        private val partMap = mapOf(
            10 to "Left-arm",// 左手
            20 to "Skirt",// 裙子
            30 to "Hair",// 头发
            40 to "Pants",// 裤子
            50 to "Sunglasses",// 太阳镜
            60 to "Left-leg",// 左腿
            70 to "Torso-skin",// 躯干皮肤
            80 to "Face",// 脸
            90 to "UpperClothes",// 上衣
            100 to "Right-leg",// 右腿
            110 to "Right-arm",// 右手
            120 to "Coat",// 外套
            130 to "Left-shoe",// 左鞋
            140 to "Right-shoe",// 右鞋
            150 to "Hat",// 帽子
            160 to "Dress",// 裙子
            170 to "Socks",// 袜子
            180 to "Scarf",// 围巾
            190 to "Gloves",// 手套
            200 to "Apparel",// 服饰
                                   )
    }

    val isSkin: Boolean
        get() {
            return pixelID in listOf(10, 40, 60, 70, 80, 100, 110, 170)
            // return pixelID in listOf(10, 60, 70, 80, 100, 110)
        }


    val partName: String
        get() {
            return partMap[pixelID] ?: "Unknown"
        }
}