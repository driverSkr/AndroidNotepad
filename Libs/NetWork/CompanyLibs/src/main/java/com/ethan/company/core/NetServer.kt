package com.ethan.company.core

import com.ethan.company.gson.BaseRObjectResult
import com.ethan.company.gson.BaseResult
import com.ethan.company.gson.IpInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface NetServer {
    @POST("{path}")
    fun postString(@Path(value = "path", encoded = true) path: String, @Body body: RequestBody): Call<BaseResult>

    @GET("{path}")
    fun getString(@Path(value = "path", encoded = true) path: String, @QueryMap(encoded = true) params: Map<String, String>): Call<BaseResult>

    @Streaming
    @GET
    fun getFile(@Url fileUrl: String): Call<ResponseBody>

    @Streaming
    @GET
    suspend fun syncGetFile(@Url fileUrl: String): Response<ResponseBody>

    @Multipart
    @POST("{path}")
    fun upFile(@Path(value = "path", encoded = true) path: String, @Part body: List<MultipartBody.Part>): Call<BaseResult>

    @POST
    suspend fun asyncPostUrl(@Url url: String, @Body body: RequestBody): Response<BaseResult>

    @POST("{path}")
    suspend fun asyncPostString(@Path(value = "path", encoded = true) path: String, @Body body: RequestBody): Response<BaseResult>

    @Headers("Cache-Control: public, max-age=" + 3600)
    @POST("{path}")
    suspend fun asyncPostStringWithCache1H(@Path(value = "path", encoded = true) path: String, @Body body: RequestBody): Response<BaseResult>

    @GET("{path}")
    suspend fun asyncGetString(@Path(value = "path", encoded = true) path: String, @QueryMap(encoded = true) params: Map<String, String>): Response<BaseResult>

    @GET("{path}")
    suspend fun asyncGetStringIp(@Path(value = "path", encoded = true) path: String): Response<IpInfo>

    @POST("{path}")
    suspend fun asyncPostStringRObject(@Path(value = "path", encoded = true) path: String, @Body body: RequestBody): Response<BaseRObjectResult>

}