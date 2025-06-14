package com.ethan.android.notepad.ui.technique.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("user_id") val userId: Int?,     //用户id
    @SerializedName("user_hash_id") val userHashId: String?,     //用户hash id
    @SerializedName("phone_number") val phoneNumber: String?,    //手机号
    @SerializedName("token") val token: String?,     //业务token
    @SerializedName("anonymous") val anonymous: String?     //是否为游客
)