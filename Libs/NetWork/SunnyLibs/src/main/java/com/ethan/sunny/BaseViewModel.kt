package com.ethan.sunny

import androidx.lifecycle.ViewModel
import retrofit2.HttpException

/**
 * ViewModel基础封装类
 */
open class BaseViewModel : ViewModel() {
    /**
     * 捕获并处理异常
     */
    fun getApiException(e: Throwable): ApiException? {
        return when (e) {
            is ApiException -> e
            is IllegalArgumentException -> e.message?.let { ApiException(it, -100) }
            is HttpException ->e.message?.let { ApiException(it,e.code(),e.response()?.errorBody()?.string()) }
            else -> e.message?.let { ApiException(it, -100) }
        }
    }
}