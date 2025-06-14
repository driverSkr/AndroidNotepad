package com.ethan.videoediting

/**
 * @Author      : Spike Peng
 * @Email       : penghaichen@tenorshare.cn
 * @Date        : on 2024/11/13 15:17.
 * @Description :
 */
object VideoEditingUtils {
    fun getTimeByMs(ms: Long): String {
        return if (ms > 0) {
            val totalSeconds = ms / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            val millis = ms % 1000
            "$hours:$minutes:$seconds.$millis"
        } else {
            "00:00:00.000"
        }
    }
}