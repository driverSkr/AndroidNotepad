package com.ethan.file

import android.content.Context
import android.os.Build
import android.text.TextUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Author: loren
 * Date: 2022/10/21
 */
object LogWriter {
    private var DATA_PATH: String? = null
    private var PATH_IMAGE: String? = null
    private var PATH_LOG: String? = null
    private var PATH_HTTP_LOG: String? = null
    var version_name: String? = null
    private var isInit = false
    private var isOpen = false

    fun init(context: Context, version_name: String) {
        if (!isInit) {
            DATA_PATH = context.getExternalFilesDir(null)?.absolutePath
            PATH_IMAGE = "$DATA_PATH/Image/"
            PATH_LOG = "$DATA_PATH/LOG/"
            PATH_HTTP_LOG = "$DATA_PATH/LOG/HTTP/"
            LogWriter.version_name = version_name
            isInit = true
            start()
        }
    }

    fun start() {
        append("start--${Build.BRAND}--${Build.MODEL}--sdk:${Build.VERSION.SDK_INT}--version:$version_name")
        val dir = File(PATH_IMAGE!!)
        if (!dir.exists()) dir.mkdirs()
    }

    fun append(str: String, isDebug: Boolean? = false) {
        if (isDebug!!) {
            try {
                if (!version_name.isNullOrEmpty()) {
                    val count = version_name!!.count { it == '.' }
                    if (count < 3) {
                        return
                    }
                } else {
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (TextUtils.isEmpty(str)) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val string = StringBuffer()
            string.append(date(System.currentTimeMillis()) + "==")
            string.append(str)
            string.append("\n")
            if (PATH_LOG == null) {
                return@launch
            }
            if (PATH_LOG?.isBlank() == true) {
                return@launch
            }
            val dir = File(PATH_LOG!!)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val log = File(dir, "log.txt")
            FileUtils.writeAppend(string.toString(), log.absolutePath)
        }
    }

    fun append2http(str: String, isDebug: Boolean? = false) {
        if (isDebug!!) {
            try {
                if (!version_name.isNullOrEmpty()) {
                    val count = version_name!!.count { it == '.' }
                    if (count < 3) {
                        return
                    }
                } else {
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (TextUtils.isEmpty(str)) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val string = StringBuffer()
            string.append(date(System.currentTimeMillis()) + "==")
            string.append(str)
            string.append("\n")
            if (PATH_HTTP_LOG == null) {
                return@launch
            }
            if (PATH_HTTP_LOG?.isBlank() == true) {
                return@launch
            }
            val dir = File(PATH_HTTP_LOG!!)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val log = File(dir, "http_log.txt")
            FileUtils.writeAppend(string.toString(), log.absolutePath)
        }
    }

    private val formatterSec = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private fun date(date: Long?): String? {
        if (date == null) {
            return ""
        }
        return formatterSec.format(Date(date))
    }

    fun path(): String? {
        val log = File(PATH_LOG, "log.txt")
        if (log.exists()) {
            return log.absolutePath
        }
        return null
    }

    fun httpLogPath(): String? {
        val log = File(PATH_HTTP_LOG, "http_log.txt")
        if (log.exists()) {
            return log.absolutePath
        }
        return null
    }

    fun getOnePoint(number: Float): String {
        return try {
            val format = String.format("%.1f", number)
            format
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}