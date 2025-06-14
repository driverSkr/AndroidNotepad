package com.ethan.company.gson

import androidx.annotation.Keep

@Keep
class BaseResult {

    val msg: String? = null
    val message: String? = null
    val code: Int? = null
    val status: Int? = null
    val data: Any? = null
    val page: PageDate? = null

    fun isSuccess(): Boolean {
        return code == 200 || status == 200
    }

    fun getCode(): Int {
        val i = code ?: status
        return i ?: -1
    }

    fun getErrorMsg(): String {
        return if (msg.isNullOrEmpty()) {
            message ?: ""
        } else {
            msg
        }
    }
}