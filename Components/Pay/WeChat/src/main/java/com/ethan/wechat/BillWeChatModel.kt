package com.ethan.wechat

import com.google.gson.annotations.SerializedName

class BillWeChatModel {

    var custom_oid: String? = null
    var body: Body? = null
    var scene: String? = null
    var token: String? = null
    var create_time: Long? = null
    var goods_name: String? = null
    var amount: String? = null
    var vendor: String? = null
    var cart: String? = null

    class Body {
        var appid: String? = null
        var noncestr: String? = null

        @SerializedName("package")
        var packageSign: String? = null
        var prepayid: String? = null
        var partnerid: String? = null
        var timestamp: Long? = null
        var sign: String? = null
    }
}