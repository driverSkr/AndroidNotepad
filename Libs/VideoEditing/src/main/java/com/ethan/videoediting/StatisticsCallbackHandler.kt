package com.ethan.videoediting

import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.Statistics

private const val TAG = "StatisticsCallbackHandl"


class StatisticsCallbackHandler {
    companion object {
        private val statisticsCallbackHandler by lazy { StatisticsCallbackHandler() }

        fun instance(): StatisticsCallbackHandler {
            return statisticsCallbackHandler
        }
    }

    private var callBackMap = mutableMapOf<Long, (Statistics) -> Unit>()

    init {
        FFmpegKitConfig.enableStatisticsCallback {
            callBackMap[it.sessionId]?.invoke(it)
        }
    }

    /**
     * 注意，在完成命令的时候请调用[removeCallBack]及时释放[callBackMap]资源。
     */
    fun addCallback(sessionId: Long, callback: (Statistics) -> Unit) {
        callBackMap[sessionId] = callback
    }


    fun removeCallBack(sessionId: Long) {
        callBackMap.remove(sessionId)
    }


}