package com.ethan.videoediting

import android.graphics.Rect
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "VideoCrop"

/**
 * @author  PengHaiChen
 * @date    2023-07-04 15:51:02
 * @email   penghaichen@tenorshare.cn
 * 图像视频裁切
 * 这个和VideoCutting不同,VideoCutting是裁切时间,这个是裁切分辨率
 */
class VideoCrop {

    private var inputPath: String? = null
    private var cropRect: Rect? = null
    private var outputPath: String? = null
    private var scaleW = -1
    private var scaleH = -1
    private var successCallback = {}
    private var failCallback = {}
    private var ssTo: Pair<Float, Float> = Pair(0F, 100F)
    private var frameRateCommand = ""
    private var scaleCommand = ""
    private var rotateAngle = 0F
    private var vFlip = false
    private var hFlip = false

    class Builder {

        private var videoCrop = VideoCrop()

        fun videoPath(path: String): Builder {
            videoCrop.inputPath = path
            return this
        }

        /**
         * 输出视频的路径
         */
        fun outputVideoPath(path: String): Builder {
            videoCrop.outputPath = path
            return this

        }

        /**
         * 成功回调
         */
        fun successCallBack(callback: () -> Unit): Builder {
            videoCrop.successCallback = callback
            return this
        }

        /**
         * 失败回调
         */
        fun failCallBack(callback: () -> Unit): Builder {
            videoCrop.failCallback = callback
            return this
        }

        /**
         * 指定帧率
         */
        fun setOutPutFrameRate(rate: Int): Builder {
            videoCrop.frameRateCommand = "-r $rate"
            return this
        }


        /**
         * 设置旋转角度
         */
        fun setRotateAngle(rotateAngle: Float): Builder {
            videoCrop.rotateAngle = rotateAngle
            return this
        }

        /**
         * 设置水平翻转
         */
        fun setHFlip(hFlip: Boolean): Builder {
            videoCrop.hFlip = hFlip
            return this
        }

        /**
         * 设置垂直翻转
         */
        fun setVFlip(vFlip: Boolean): Builder {
            videoCrop.vFlip = vFlip
            return this
        }

        /**
         * 设置目标矩形的Rect
         * 注意, 如果无需crop,请传null,如果rect为0 0 0 0也不会进行裁切
         */
        fun setCropRect(cropRect: Rect?): Builder {
            videoCrop.cropRect = cropRect
            return this
        }

        fun setSSto(ssTo: Pair<Float, Float>?): Builder {
            if (ssTo != null) {
                videoCrop.ssTo = ssTo
            }
            return this
        }

        /**
         * 缩放,注意,因为视频的特殊性,请传递更为精准的宽高值,不允许传递比例
         */
        fun setScale(scaleW: Int, scaleH: Int): Builder {
            videoCrop.scaleW = scaleW
            videoCrop.scaleH = scaleH
            return this
        }

        fun build(): VideoCrop {
            return videoCrop
        }

    }

    fun execution() {
        val videoInfo = inputPath?.let { FfmpegVE.getVideoInfo(it) } ?: return
        val videoWidth = videoInfo.videoCodedWidth ?: return
        val videoHeight = videoInfo.videoCodedHeight ?: return
        val formattedStartTime = videoInfo.videoDuration * ssTo.first
        val formattedEndTime = videoInfo.videoDuration * ssTo.second
        val videoVFlip = if (vFlip) "vflip," else ""
        val videoHFlip = if (hFlip) "hflip," else ""
        val scaleCommend = if (scaleW != -1 && scaleH != -1) {
            ",scale=w=$scaleW:h=$scaleH:force_original_aspect_ratio=decrease,pad=w=$scaleW:h=$scaleH:x=(ow-iw)/2:y=(oh-ih)/2:color=black"
        } else {
            ""
        }
        val rotateAngle = when {
            rotateAngle % 360F == 90F -> "transpose=1,"
            rotateAngle % 360F == 180F -> "rotate=180*PI/180,"
            rotateAngle % 360F == 270F -> "transpose=2,"
            else -> ""
        }
        val command =
            if (cropRect == null || (cropRect?.left == 0 && cropRect?.top == 0 && cropRect?.right == 0 && cropRect?.bottom == 0)) {
                failCallback.invoke()
                return
            } else {
                "-ss $formattedStartTime -to $formattedEndTime -i '$inputPath' -vf \"${videoVFlip}${videoHFlip}${rotateAngle}crop=w=${cropRect!!.width()}:h=${cropRect!!.height()}:x=${cropRect!!.left}:y=${cropRect!!.top}${scaleCommend}\" $frameRateCommand '$outputPath'"
            }
        Log.d(TAG, "execution: $command")
        CoroutineScope(Dispatchers.Default).launch {
            val execute = FFmpegKit.execute(command)
            if (ReturnCode.isSuccess(execute.returnCode)) {
                withContext(Dispatchers.Main) {
                    successCallback.invoke()
                }
            } else {
                withContext(Dispatchers.Main) {
                    failCallback.invoke()
                }
            }
        }
    }

}