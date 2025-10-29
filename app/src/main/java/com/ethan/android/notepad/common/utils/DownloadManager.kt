package com.ethan.android.notepad.common.utils

import com.ethan.android.notepad.common.model.download.DownloadProgress
import com.ethan.android.notepad.common.model.download.DownloadResult
import com.ethan.android.notepad.common.model.download.DownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class DownloadManager private constructor(){
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("")    // 基础URL，实际下载会使用完整URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val downloadService: DownloadService by lazy {
        retrofit.create(DownloadService::class.java)
    }

    private val downloads = mutableMapOf<String, Job>()

    companion object {
        @Volatile
        private var instance: DownloadManager? = null

        fun getInstance(): DownloadManager {
            return instance ?: synchronized(this) {
                instance ?: DownloadManager().also { instance = it }
            }
        }
    }

    /**
     * 开始或恢复下载
     */
    fun downloadFile(
        url: String,
        savePath: File,
        fileName: String,
        coroutineScope: CoroutineScope,
        onProgress: (DownloadProgress) -> Unit = {},
        onResult: (DownloadResult) -> Unit = {}
    ) {
        val downloadKey = "$url-$fileName"

        // 如果已经在下载，先取消
        downloads[downloadKey]?.cancel()

        val job = coroutineScope.launch(Dispatchers.IO) {
            try {
                performDownload(url, savePath, fileName, onProgress, onResult)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(DownloadResult(error = e.message ?: "下载失败"))
                }
            }
        }

        downloads[downloadKey] = job
        job.invokeOnCompletion {
            downloads.remove(downloadKey)
        }
    }

    /**
     * 取消下载
     */
    fun cancelDownload(url: String, fileName: String) {
        val downloadKey = "$url-$fileName"
        downloads[downloadKey]?.cancel()
        downloads.remove(downloadKey)
    }

    /**
     * 执行下载操作
     */
    private suspend fun performDownload(
        url: String,
        savePath: File,
        fileName: String,
        onProgress: (DownloadProgress) -> Unit,
        onResult: (DownloadResult) -> Unit
    ) {

    }
}