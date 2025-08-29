//package com.ethan.android.notepad.model
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Canvas
//import android.graphics.Matrix
//import android.graphics.Paint
//import android.graphics.Rect
//import android.util.Log
//import androidx.core.graphics.values
//import com.blankj.utilcode.util.ImageUtils
//import com.ethan.android.notepad.base.Constants
//import kotlin.coroutines.resume
//import kotlin.coroutines.suspendCoroutine
//
//object FilterGenerator {
//
////    fun getFilterBitmapByLut(srcBitmap: Bitmap?, lutBitmapPath: String?): Bitmap? {
////        return getFilterBitmapByLut(srcBitmap, BitmapFactory.decodeFile(lutBitmapPath))
////    }
////
////    fun getFilterBitmapByLut(srcBitmapPath: String?, lutBitmapPath: String?): Bitmap? {
////        return getFilterBitmapByLut(BitmapFactory.decodeFile(srcBitmapPath), BitmapFactory.decodeFile(lutBitmapPath))
////    }
////
////    fun getFilterBitmapByLut(srcBitmap: Bitmap?, lutBitmap: Bitmap?): Bitmap? {
////        if (lutBitmap == null || srcBitmap == null) {
////            return null
////        }
////        return EasyLUT.fromBitmap().withBitmap(lutBitmap).createFilter().apply(srcBitmap)
////    }
//
//    /**
//     * @param currentPath 图地址
//     * @param proportion 预览略缩的比例
//     * @param filterData 滤镜数据
//     */
//    suspend fun genFinisFilterImagePath(
//        currentPath: String,
//        proportion: Float,
//        filterData: FilterMaterialPkgDownloadViewModel.FilterPath?,
//                                       ): String = suspendCoroutine { continuation ->
//        Log.i(TAG, "genFinisFilterImage: $filterData")
//        val originBitmap = if (filterData?.filterPath.isNullOrEmpty()) {
//            BitmapFactory.decodeFile(currentPath, BitmapFactory.Options()
//                .apply { this.inMutable = true })
//        } else {
//            getFilterBitmapByLut(currentPath, filterData?.filterPath)!!
//        }
//        var filterStickerBitmap = BitmapFactory.decodeFile(filterData?.stickerPath)
//        if (filterStickerBitmap == null) {
//            continuation.resume(currentPath)
//            return@suspendCoroutine
//        }
//        val scale = DimensionCalculationUtils.matchAll(Rect(0, 0, filterStickerBitmap.width, filterStickerBitmap.height), originBitmap.width, originBitmap.height)
//        filterStickerBitmap = ImageUtils.scale(filterStickerBitmap, scale, scale)
//        Log.i(TAG, "genFinisFilterImage: proportion - $proportion")
//        Log.i(TAG, "genFinisFilterImage: scale - $scale")
//        val matrixScale = Matrix().apply { this.postScale(1 / proportion, 1 / proportion) }
//        val matrix = Matrix(filterData?.stickerMatrix)
//        val matrixTransValue = floatArrayOf(matrix.values()[Matrix.MTRANS_X], matrix.values()[Matrix.MTRANS_Y])
//        matrixScale.mapPoints(matrixTransValue)
//        matrix.setValues(matrix.values().apply {
//            this[Matrix.MTRANS_X] = matrixTransValue[0]
//            this[Matrix.MTRANS_Y] = matrixTransValue[1]
//        })
//        val canvas = Canvas(originBitmap)
//        canvas.concat(matrix)
//        canvas.drawBitmap(filterStickerBitmap, 0F, 0F, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG))
//
//        val filepath = Constants.PATH_IMAGE + "remove_object_filter_" + System.currentTimeMillis() + ".JPEG"
//        ImageUtils.save(originBitmap, filepath, Bitmap.CompressFormat.JPEG, 100)
//        continuation.resume(filepath)
//    }
//
//    suspend fun genFinisFilterImage(
//        currentPath: String,
//        proportion: Float,
//        filterData: FilterMaterialPkgDownloadViewModel.FilterPath?,
//        dstWH: Rect? = null,
//                                   ): Bitmap? = suspendCoroutine { continuation ->
//        val originBitmap = if (filterData?.filterPath.isNullOrEmpty()) {
//            val decodeFile = BitmapFactory.decodeFile(currentPath, BitmapFactory.Options()
//                .apply { this.inMutable = true })
//            if (dstWH != null) {
//                ImageUtils.scale(decodeFile, dstWH.width(), dstWH.height(), true)
//            } else {
//                decodeFile
//            }
//        } else {
//            val decodeFile = BitmapFactory.decodeFile(currentPath, BitmapFactory.Options()
//                .apply { this.inMutable = true })
//            val scaleDecodeFile = if (dstWH != null) {
//                ImageUtils.scale(decodeFile, dstWH.width(), dstWH.height(), true)
//            } else {
//                decodeFile
//            }
//            getFilterBitmapByLut(scaleDecodeFile, filterData?.filterPath)!!
//        }
//        var filterStickerBitmap = BitmapFactory.decodeFile(filterData?.stickerPath)
//        if (filterStickerBitmap == null) {
//            continuation.resume(originBitmap)
//            return@suspendCoroutine
//        }
//        val scale = DimensionCalculationUtils.matchAll(Rect(0, 0, filterStickerBitmap.width, filterStickerBitmap.height), originBitmap.width, originBitmap.height)
//        filterStickerBitmap = ImageUtils.scale(filterStickerBitmap, scale, scale)
//        Log.i(TAG, "genFinisFilterImage: proportion - $proportion")
//        Log.i(TAG, "genFinisFilterImage: scale - $scale")
//        val matrixScale = Matrix().apply { this.postScale(1 / proportion, 1 / proportion) }
//        val matrix = Matrix(filterData?.stickerMatrix)
//        val matrixTransValue = floatArrayOf(matrix.values()[Matrix.MTRANS_X], matrix.values()[Matrix.MTRANS_Y])
//        matrixScale.mapPoints(matrixTransValue)
//        matrix.setValues(matrix.values().apply {
//            this[Matrix.MTRANS_X] = matrixTransValue[0]
//            this[Matrix.MTRANS_Y] = matrixTransValue[1]
//        })
//        val canvas = Canvas(originBitmap)
//        canvas.concat(matrix)
//        canvas.drawBitmap(filterStickerBitmap, 0F, 0F, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG))
//        continuation.resume(originBitmap)
//        return@suspendCoroutine
//    }
//}