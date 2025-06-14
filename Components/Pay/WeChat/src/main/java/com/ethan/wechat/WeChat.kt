package com.ethan.wechat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlin.concurrent.thread


object WeChat {

    const val WECHAT_APPID = "wx164c73f32ed2bd29"
    const val WX_PAY_RECEIVE_ACTION = "com.niuxuezhang.huanlianai.wx_pay_receive_action"

    lateinit var iwxapi: IWXAPI

    fun init(context: Context) {
        iwxapi = WXAPIFactory.createWXAPI(context, WECHAT_APPID, true)
        iwxapi.registerApp(WECHAT_APPID)
    }

    fun wechat(body: BillWeChatModel.Body?, context: Context, callback: OnWeChatResultCallback) {
        val wxPayReceive: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == WX_PAY_RECEIVE_ACTION) {
                    val errCode = intent.getIntExtra("errCode", -1)
                    when (errCode) {
                        0 -> {
                            callback.onSuccess()
                        }
                        -2 -> {
                            callback.onCancel()
                        }
                        else -> {
                            callback.onFailure("")
                        }
                    }
                    context.unregisterReceiver(this)
                }
            }
        }
        val filter = IntentFilter()
        filter.addAction(WX_PAY_RECEIVE_ACTION)
        context.registerReceiver(wxPayReceive, filter)
        /*val iwxapi = WXAPIFactory.createWXAPI(context, body?.appid, true)
        iwxapi.registerApp(body?.appid)*/
        if (iwxapi.isWXAppInstalled) {
            thread {
                val request = PayReq() //调起微信APP的对象
                request.appId = body?.appid
                request.partnerId = body?.partnerid
                request.prepayId = body?.prepayid
                request.packageValue = body?.packageSign
                request.nonceStr = body?.noncestr
                request.timeStamp = body?.timestamp.toString()
                request.sign = body?.sign
                iwxapi.sendReq(request) //发送调起微信的请求
            }
        } else {
            callback.onNotInstall()
        }
    }

}