package com.ethan.pay.model

/**
 * Author: loren
 * Date: 2022/6/24
 */
interface OnPayResultCallback {
    fun begin()
    fun onSuccess(orderList: MutableList<OrderInfo>)
    fun onOwned(orderList: MutableList<OrderInfo>)
    fun onFailed(msg: String?)
    fun onDisconnect()
    fun onCancel()
}