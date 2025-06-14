package com.ethan.imagehandler

import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.Rect
import com.ethan.imagehandler.bean.ObjremPreProcessingBean
import com.ethan.imagehandler.bean.SkinSegBean

class ImageNativeUtils {


    external fun getHasPixelRectSize(bitmap: Bitmap): Rect
    external fun getConnectedArea(bitmap: Bitmap, rectList: ArrayList<Rect>)

    external fun getEdgePathByCv(bitmap: Bitmap, contours: IntArray): Path
    external fun getEdgeBitmapByCv(srcBitmap: Bitmap, maskBitmap: Bitmap, imgRect: IntArray): Bitmap
    external fun getRgba2GrayByCv(maskBitmap: Bitmap): Bitmap

    // 柳吉那边的前处理
    external fun objremPreProcessingPy(bitmapIn: Bitmap, bitmapMask: Bitmap, bitmapConfig: Bitmap.Config): ObjremPreProcessingBean?

    // 泊远那边的前处理
    external fun objremPreProcessingCpp(bitmapIn: Bitmap, bitmapMask: Bitmap, bitmapConfig: Bitmap.Config): ObjremPreProcessingBean?
    external fun objremPreProcessingMerge(bitmapIn: Bitmap, bitmapMask: Bitmap, bitmapConfig: Bitmap.Config): Bitmap?
    external fun objremAfterProcessing(
        originImage: Bitmap, image: Bitmap, mask: Bitmap, bitmapConfig: Bitmap.Config,
        x: Int, y: Int, w: Int, h: Int,
        maskX: Int, maskY: Int, maskW: Int, maskH: Int,
                                      ): Bitmap?


    external fun getMixSrcAndAlpha(srcBitmap: Bitmap, maskBitmapAlpha: Bitmap, filepath: String, resize: Int): Boolean

    /**
     *  添加部分后处理操作
     *  将Mask覆盖的像素放到原图上
     */
    external fun redrawPostProcessing(originImage: String, uploadImage: String, resultImage: String, savePath: String, pixelCount: Int)

    /**
     * 水平拼接两张图片
     */
    external fun horizontalPuzzle(img1: Bitmap, img2: Bitmap, path: String): Boolean

    external fun getMaskByDepth(image: String, savePath: String, segList: ArrayList<SkinSegBean>)

    /**
     * 高斯模糊
     * kernel_size 必须是正奇数
     */
    external fun nativeGaussianBlur(path: String,ikernelSize: Int): Bitmap


    companion object {

        init {
            System.loadLibrary("native_image_lib")
        }

        private val mImageNativeUtils: ImageNativeUtils by lazy {
            ImageNativeUtils()
        }

        fun getInstance(): ImageNativeUtils {
            return mImageNativeUtils
        }


    }


}