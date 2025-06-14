package com.ethan.imagehandler

import android.content.res.AssetManager
import android.graphics.Bitmap
import com.ethan.imagehandler.bean.FaceBean

class NCNNFaceDetectUtils {
    @Synchronized
    external fun loadModel(mgr: AssetManager?, modelid: Int = 2, cpugpu: Int = 0): Boolean

    @Synchronized
    private external fun detailFaceByBitmapNDK(bitmap: Bitmap, faceList: ArrayList<FaceBean>, needThumb: Boolean = false): Boolean

    @Synchronized
    fun detailFaceByBitmap(bitmap: Bitmap, faceList: ArrayList<FaceBean>, needThumb: Boolean = false): Boolean {
        try {
            return detailFaceByBitmapNDK(bitmap, faceList, needThumb)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    companion object {
        init {
            System.loadLibrary("native_image_lib")
        }

        private val ncnnFaceDetectUtils: NCNNFaceDetectUtils by lazy {
            NCNNFaceDetectUtils()
        }

        fun getInstance(): NCNNFaceDetectUtils {
            return ncnnFaceDetectUtils
        }
    }
}