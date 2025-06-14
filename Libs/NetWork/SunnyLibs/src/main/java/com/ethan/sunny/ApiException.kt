package com.ethan.sunny

/**
 * 封装网络请求错误
 */
class ApiException(val errorMessage: String, val errorCode: Int,var errorbody:String? = null) : Throwable()