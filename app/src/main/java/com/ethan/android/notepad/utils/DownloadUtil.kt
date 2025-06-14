package com.ethan.android.notepad.utils

import android.util.Log
import com.blankj.utilcode.util.FileUtils
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * 文件下载工具，附带下载进度
 */
class DownloadUtil {

    private val okHttpClient: OkHttpClient = OkHttpClient()
    private var nowCall: Call? = null
    private var myListener: OnDownloadListener? = null
    private var isCancel = false
    private var isDownLoad = false

    companion object {
        private var downloadUtil: DownloadUtil? = null
        fun get(): DownloadUtil? {
            if (downloadUtil == null) {
                downloadUtil = DownloadUtil()
            }
            return downloadUtil
        }

        /**
         * url
         * 从下载连接中解析出文件名
         */
        fun getNameFromUrl(url: String): String {
            return url.substring(url.lastIndexOf("/") + 1)
        }
    }

    /**
     * url 下载链接
     * saveDir 存储下载文件的SDCard目录
     * listener 下载监听
     */
    fun download(url: String, saveDir: String, listener: OnDownloadListener) {
        val request: Request = Request.Builder().url(url).build()
        nowCall = okHttpClient.newCall(request)
        myListener = listener
        isCancel = false
        isDownLoad = false
        nowCall?.enqueue( object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                //下载失败
                if (!isCancel && !call.isCanceled()) {
                    listener.onDownloadFailed(true)
                }
                Log.e("DownloadUtil", "onFailure: " + e.message)
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                isDownLoad = true
                var `is`: InputStream? = null
                val buf = ByteArray(2048)
                var len: Int
                var fos: FileOutputStream? = null
                //存储下载文件的目录
                try {
                    `is` = response.body!!.byteStream()
                    val total = response.body!!.contentLength()
                    val file = File(saveDir)
                    //不完整的文件直接删除
                    if (file.exists() && file.length() < total) {
                        FileUtils.delete(file)
                        //文件不完整
                        Log.e("DownloadUtil", "文件不完整")
                    }

                    fos = FileOutputStream(file)
                    var sum: Long = 0
                    while (`is`.read(buf).also { len = it } != -1) {
                        fos.write(buf, 0 , len)
                        sum += len.toLong()
                        val progress = (sum * 1.0f / total * 100).toInt()
                        //下载中
                        listener.onDownloading(progress)
                    }
                    fos.flush()
                    //下载完成
                    listener.onDownloadSuccess()
                    isDownLoad = false
                } catch (e: Exception) {
                    isDownLoad = false
                    if (!isCancel) {
                        if (nowCall?.isCanceled() == true) {
                            listener.onDownloadFailed(false)
                            Log.e("DownloadUtil", "onFailure: " + e.message)
                        } else {
                            listener.onDownloadFailed(true)
                        }
                    }
                } finally {
                    try {
                        `is`?.close()
                    } catch (_: IOException) {}
                    try {
                        fos?.close()
                    } catch (_: IOException) {}
                }
            }

        })
    }

    fun stopDownload() {
        if (isDownLoad) {
            nowCall?.cancel()
            myListener?.onDownloadCancel()
            isCancel = true
            isDownLoad = false
        }
    }

    interface OnDownloadListener {
        /**
         * 下载成功
         */
        fun onDownloadSuccess()

        /**
         * @param progress
         */
        fun onDownloading(progress: Int)

        /**
         * 下载失败
         */
        fun onDownloadFailed(boolean: Boolean)

        /**
         * 下载失败
         */
        fun onDownloadCancel()
    }
}