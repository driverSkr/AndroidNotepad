package com.ethan.company.workApi

import com.ethan.company.NetConfig
import com.ethan.company.gson.BaseResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface TattooApi {

    @POST(NetConfig.AI_TATTOO_SKIN_SEG)
    fun doSkinSeg(@Body body: RequestBody): Call<BaseResult>

}