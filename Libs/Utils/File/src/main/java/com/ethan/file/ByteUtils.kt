package com.ethan.file

import java.text.DecimalFormat

/**
 * 返回byte的数据大小对应的文本
 */
object ByteUtils {
    private val df = DecimalFormat("0.00")

    /**
     * @param size
     * @return
     */
    fun sizeFormat(size: Long): String {
        val kb: Long = 1024
        val mb = kb * 1024
        val gb = mb * 1024
        val tb = gb * 1024
        return when {
            size < mb -> {
                df.format((size / 1024f).toDouble()) + " KB"
            }
            size < gb -> {
                df.format((size / 1024f / 1024f).toDouble()) + " MB"
            }
            size < tb -> {
                df.format((size / 1024f / 1024f / 1024f).toDouble()) + " GB"
            }
            else -> {
                df.format((size / 1024f / 1024f / 1024f / 1024f).toDouble()) + " TB"
            }
        }
    }
}