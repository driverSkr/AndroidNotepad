package com.ethan.android.notepad.common.model.download

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

// Retrofit 接口
interface DownloadService {
    @Streaming
    @GET
    suspend fun downloadFile(
        @Url url: String,
        @Header("Range") range: String? = null
    ): retrofit2.Response<ResponseBody>
}