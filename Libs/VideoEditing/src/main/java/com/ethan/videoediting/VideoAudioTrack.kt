package com.ethan.videoediting

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class VideoAudioTrack {
    private var inputPath: String? = null
    private var outputPath: String? = null
    private var audioPath: String? = null
    private var successCallback = {}
    private var failCallback = {}

    class Builder {
        private var videoAudioTrack = VideoAudioTrack()


        /**
         * 输出视频的路径,请给一个文件路径
         */
        fun outputVideoPath(path: String): Builder {
            videoAudioTrack.outputPath = path
            return this

        }

        fun inputVideoPath(path: String): Builder {
            videoAudioTrack.inputPath = path
            return this
        }


        /**
         * 成功回调
         */
        fun successCallBack(callback: () -> Unit): Builder {
            videoAudioTrack.successCallback = callback
            return this
        }

        /**
         * 失败回调
         */
        fun failCallBack(callback: () -> Unit): Builder {
            videoAudioTrack.failCallback = callback
            return this
        }


        fun audioFile(audioPath: String): Builder {
            videoAudioTrack.audioPath = audioPath
            return this
        }

        fun build(): VideoAudioTrack {
            return videoAudioTrack
        }
    }


    /**
     * 处理
     */
    fun execution() {
        CoroutineScope(Dispatchers.Default).launch {
            val command = "-i '$inputPath' -i '$audioPath' -c:v copy -c:a copy -map 0:v:0 -map 1:a:0 -shortest '${outputPath}'"
            val session = FFmpegKit.execute(command)
            if (ReturnCode.isSuccess(session.returnCode)) {
                successCallback.invoke()
            } else {
                failCallback.invoke()
            }

        }
    }
}