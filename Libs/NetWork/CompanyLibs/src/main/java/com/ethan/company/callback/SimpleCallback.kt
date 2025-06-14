package com.ethan.company.callback

import com.ethan.company.gson.BaseResult


interface SimpleCallback {

    fun onSuccess(result: BaseResult?)

    fun onFailure(code: Int, msg: String?)
}