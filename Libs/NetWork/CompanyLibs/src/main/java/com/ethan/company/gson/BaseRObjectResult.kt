package com.ethan.company.gson

import com.google.gson.annotations.SerializedName

class BaseRObjectResult {
    val msg: String? = null
    val message: String? = null
    val code: Int? = null
    val data: Data? = null

    fun isSuccess(): Boolean {
        return code == 200
    }

    fun getCode(): Int {
        val i = code ?: -1
        return i ?: -1
    }

    fun getErrorMsg(): String {
        return if (msg.isNullOrEmpty()) {
            message ?: ""
        } else {
            msg
        }
    }

    class Data{
        @SerializedName("request_file")
        val requestFile: ArrayList<String>? = null
        @SerializedName("response_file")
        val responseFile: ArrayList<String>? = null
        val result: result? = null

        override fun toString(): String {
            return "Data(requestFile=$requestFile, responseFile=$responseFile, result=$result)"
        }
    }


    class result{
        @SerializedName("job_id")
        val jobId: String? = null

        override fun toString(): String {
            return "result(jobId=$jobId)"
        }
    }

    override fun toString(): String {
        return "BaseRObjectResult(msg=$msg, message=$message, code=$code, data=$data)"
    }
}