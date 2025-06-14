package com.ethan.alipay


interface OnAliPayResultCallback {
    fun onSuccess()
    fun onCancel()
    fun onFailure(errorMessage: String?)
}