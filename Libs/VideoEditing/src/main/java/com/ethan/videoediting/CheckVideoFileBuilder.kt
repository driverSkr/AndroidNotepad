package com.ethan.videoediting

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckVideoFileBuilder {
    private var inputPath: String? = null
    private var successCallback = {}
    private var failCallback = {}
    class Builder {
        private var checkVideo = CheckVideoFileBuilder()
        fun inputVideoPath(path: String): Builder {
            checkVideo.inputPath = path
            return this
        }

        /**
         * 成功回调
         */
        fun successCallBack(callback: () -> Unit): Builder {
            checkVideo.successCallback = callback
            return this
        }

        /**
         * 失败回调
         */
        fun failCallBack(callback: () -> Unit): Builder {
            checkVideo.failCallback = callback
            return this
        }
        fun build(): CheckVideoFileBuilder {
            return checkVideo
        }
    }
    /**
     * 处理
     */
    fun execution() {
        CoroutineScope(Dispatchers.Default).launch {

            val command = "-v error -i '$inputPath' -map 0:1 -f null - "
            val session = FFmpegKit.execute(command)
            if (ReturnCode.isSuccess(session.returnCode)) {
                successCallback.invoke()
            } else {
                failCallback.invoke()
            }

        }
    }
}