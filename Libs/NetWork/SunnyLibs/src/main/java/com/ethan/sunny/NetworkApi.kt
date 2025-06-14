package com.ethan.sunny

import com.blankj.utilcode.util.Utils
import com.ethan.company.NetConfig
import com.ethan.company.logging.Level
import com.ethan.company.logging.LoggingInterceptor
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform.Companion.INFO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 *@time : 2021/8/13
 *@author : tzy
 */
internal val apiService: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ApiService::class.java, NetConfig.STS_SERVER_DOMAIN)
}

internal val apiResService: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ApiService::class.java, NetConfig.BASE_TEST_PAG_URL)
}

internal val apiFeedbackService: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ApiService::class.java, NetConfig.FEEDBACK_URL)
}

internal val apiCMSLogService: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ApiService::class.java, NetConfig.URL_BASE_UPLOAD)
}

/**
 * 网络请求构建器，继承BaseNetworkApi 并实现setHttpClientBuilder/setRetrofitBuilder方法，
 * 在这里可以添加拦截器，设置构造器可以对Builder做任意操作
 */
internal class NetworkApi : BaseNetworkApi() {

    companion object {
        val INSTANCE: NetworkApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkApi()
        }
    }

    /**
     * 实现重写父类的setOkHttpClientBuilder方法，
     * 在这里可以添加拦截器，可以对 OkHttpClient.Builder 做任意操作
     */
    override fun setOkHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        return builder.apply {
            //设置缓存空间为10M
            cache(Cache(File(Utils.getApp().cacheDir, "info_cache"), 10 * 1024 * 1024L))
            if (BuildConfig.DEBUG||NetConfig.InternalTester)
            {
              addInterceptor(LoggingInterceptor.Builder().setLevel(Level.BASIC).log(INFO).build())
            }
            //超时时间 连接、读、写
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
        }
    }

    /**
     * 设置Retrofit解析器
     */
    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    }

}