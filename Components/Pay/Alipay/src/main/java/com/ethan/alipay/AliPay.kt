package com.ethan.alipay

import android.app.Activity
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.WorkerThread
import com.alipay.sdk.app.PayTask


object AliPay {
    /**
     * 支付宝支付
     */
    @WorkerThread
    fun alipay(body: String?, activity: Activity?, callback: OnAliPayResultCallback) {
        val alipay = PayTask(activity)
        //true: 支付宝客户端唤起之前弹出loading弹窗
        val result = alipay.payV2(body, true)
        val status = result["resultStatus"]
        //val res = result["result"]
        //val memo = result["memo"]
        if (!TextUtils.isEmpty(status) && status == "9000") {
            callback.onSuccess()
        } else if (!TextUtils.isEmpty(status) && status == "6001") {
            callback.onCancel()
        } else {
            if (status == "4001") {
                activity?.let { Toast.makeText(it, "支付宝未安装", Toast.LENGTH_SHORT).show() }
            } else if (status == "5000") {
                activity?.let { Toast.makeText(it, "操作太频繁", Toast.LENGTH_SHORT).show() }
            }
            callback.onFailure(status)
        }
    }
}