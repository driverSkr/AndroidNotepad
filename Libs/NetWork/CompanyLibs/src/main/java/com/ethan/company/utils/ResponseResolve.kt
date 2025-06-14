package com.ethan.company.utils

import android.util.Log
import com.ethan.company.gson.BaseResult
import com.google.gson.Gson
import retrofit2.Response

private const val TAG = "ResponseResolve"


fun Response<BaseResult>.handleResponse(): BaseResult? {
    return if (this.body() == null) {
        try {
            val errorBody = this.errorBody()?.string()
            Log.e(TAG, "handleResponse: error body $errorBody")
            val result = Gson().fromJson(errorBody, BaseResult::class.java)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } else {
        this.body()
    }
}