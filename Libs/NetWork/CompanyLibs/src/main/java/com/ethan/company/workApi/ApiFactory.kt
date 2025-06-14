package com.ethan.company.workApi

import com.ethan.company.BuildConfig
import com.ethan.company.NetConfig
import com.ethan.company.core.CommonHeaders
import com.ethan.company.core.HttpEngineImpl
import com.ethan.company.logging.Level
import com.ethan.company.logging.LoggingInterceptor
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author  PengHaiChen
 * @date    2024/3/25 14:28
 * @email   penghaichen@tenorshare.cn
 */
object ApiFactory {

    val objRem: ObjRemApi by lazy {
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .baseUrl(NetConfig.STS_SERVER_DOMAIN).client(getHttpClient()).build().create(ObjRemApi::class.java)
    }

    val tattoo: TattooApi by lazy {
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .baseUrl(NetConfig.STS_SERVER_DOMAIN).client(getHttpClient()).build().create(TattooApi::class.java)
    }


    private fun getHttpClient(): OkHttpClient { // log interceptor
        // headers
        val headers = CommonHeaders.getInstance().getCommonHeaders()
        val interceptor = Interceptor { chain ->
            try {
                val request = chain.request()
                val requestBuilder = request.newBuilder()
                val url = request.url
                val builder = url.newBuilder()
                requestBuilder.url(builder.build()).method(request.method, request.body)
                headers.forEach {
                    requestBuilder.addHeader(it.key, it.value.toString())
                }
                chain.proceed(requestBuilder.build())
            } catch (e: Exception) {
                return@Interceptor Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(HttpEngineImpl.errorCode)
                    .message("client config invalid").body("client config invalid".toResponseBody(null)).build()
            }
        }
        val builder = OkHttpClient.Builder().readTimeout(NetConfig.timeout, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor)

        if (BuildConfig.DEBUG || NetConfig.InternalTester) {
            builder.addInterceptor(LoggingInterceptor.Builder().setLevel(Level.BASIC).log(Platform.INFO).build())
        }
        return builder.build()
    }
}