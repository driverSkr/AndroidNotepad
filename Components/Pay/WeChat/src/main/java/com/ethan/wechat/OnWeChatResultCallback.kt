package com.ethan.wechat

interface OnWeChatResultCallback {
    fun onSuccess()
    fun onCancel()
    fun onFailure(errorMessage: String)
    fun onNotInstall()
}