package com.ethan.android.notepad.ui.technique.model

import com.google.gson.annotations.SerializedName

data class SubInfoModel(
    @SerializedName("product_name") val productName: String?,     //产品名称
    @SerializedName("status") val status: String?,     //Active:有效，Expired：过期
    @SerializedName("create_time") val createTime: String?,    //创建时间，eg:2024-04-09 15:29:00
    @SerializedName("active_Time") var activeTime: String?,     //过期时间
    @SerializedName("create_timetimestamp") val createTimetimestamp: Int?,     //创建时间（时间戳）
    @SerializedName("expire_timestamp") val expireTimestamp: Int?,       //过期时间（时间戳）
    @SerializedName("coins") val coins: Int?,
    @SerializedName("total_coins") val totalCoins: Int?
)