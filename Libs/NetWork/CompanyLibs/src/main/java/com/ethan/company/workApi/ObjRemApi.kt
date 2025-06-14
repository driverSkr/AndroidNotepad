package com.ethan.company.workApi

import com.ethan.company.NetConfig
import com.ethan.company.gson.BaseResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ObjRemApi {

    @POST(NetConfig.REMOVE_OBJECT_SEG)
    fun doSeg(@Body body: RequestBody): Call<BaseResult>

    @POST(NetConfig.REMOVE_OBJECT_SEG_VIP)
    fun doSegVip(@Body body: RequestBody): Call<BaseResult>

}