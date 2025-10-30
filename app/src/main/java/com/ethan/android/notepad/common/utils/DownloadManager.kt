package com.ethan.android.notepad.common.utils

import com.ethan.android.notepad.common.model.download.DownloadProgress
import com.ethan.android.notepad.common.model.download.DownloadResult
import com.ethan.android.notepad.common.model.download.DownloadService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

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
        val outputFile = File(savePath, fileName)
        val downloadedSize = if (outputFile.exists()) outputFile.length() else 0L

        // 发送下载请求
        val rangeHeader = if (downloadedSize > 0) "bytes=$downloadedSize-" else null
        val response = downloadService.downloadFile(url, rangeHeader)

        if (!response.isSuccessful) {
            throw Exception("HTTP error: ${response.code()}")
        }

        val responseBody = response.body() ?: throw Exception("Response body is null")

        // 获取文件总大小
        val totalSize = if (response.code() == 206) { // Partial Content部分内容
            val contentRange = response.headers()["Content-Range"]
            contentRange?.substringAfter("/")?.toLongOrNull() ?: (downloadedSize + responseBody.contentLength())
        } else {
            responseBody.contentLength()
        }

        // 创建文件输出流（追加模式）
        val outputStream = if (downloadedSize > 0) {
            FileOutputStream(outputFile,true)
        } else {
            FileOutputStream(outputFile)
        }

        var currentBytes = downloadedSize
        var lasProgress = 0f

        try {
            responseBody.byteStream().use { inputStream ->
                outputStream.use { outputStream ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        // 检查协程是否被取消
                        if (!coroutineContext.isActive) {
                            throw CancellationException("Download cancelled")
                        }

                        outputStream.write(buffer, 0, bytesRead)
                        currentBytes += bytesRead

                        // 计算进度并回调（避免频繁回调）
                        val progress = if (totalSize > 0) {
                            currentBytes.toFloat() / totalSize.toFloat()
                        } else 0f

                        // 只有当进度有显著变化时才回调（每1%的变化）
                        if (progress - lasProgress >= 0.01f || currentBytes == totalSize) {
                            lasProgress = progress

                            withContext(Dispatchers.Main) {
                                onProgress(
                                    DownloadProgress(
                                        currentBytes = currentBytes,
                                        totalBytes = totalSize,
                                        progress = progress,
                                        isDownloading = true
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 下载完成
            withContext(Dispatchers.Main) {
                onResult(DownloadResult(file = outputFile, isCompleted = true))
            }
        } catch (e: CancellationException) {
            // 下载被取消，清理部分下载的文件（可选）
            // if (currentBytes == 0L) outputFile.delete()
        } catch (e: Exception) {
            // 下载出错
            throw e
        }
    }

    /**
     * 检查服务器是否支持断点续传
     */
    suspend fun checkResumeSupport(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            var response: Response<ResponseBody>? = null
            try {
                response = downloadService.downloadFile(url)
                val acceptRanges = response.headers()["Accept-Ranges"]
                val contentLength = response.headers()["Content-Length"]

                response.body()?.close()
                acceptRanges == "bytes" && contentLength != null
            } catch (e: Exception) {
                // 确保在异常时也关闭相应
                response?.body()?.close()
                false
            }
        }
    }

    /**
     * 获取已下载文件大小
     */
    fun getDownloadedSize(savePath: File, fileName: String): Long {
        val file = File(savePath, fileName)
        return if (file.exists()) file.length() else 0L
    }
}