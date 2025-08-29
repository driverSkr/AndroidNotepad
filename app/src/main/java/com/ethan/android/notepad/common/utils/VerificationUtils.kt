package com.ethan.android.notepad.common.utils

object VerificationUtils {

    // 校验手机号是否合法
    fun validatePhoneNumber(phone: String): Boolean {
        val regex = Regex("^(1[3-9]\\d{9}|(147|148|166|195|198|199)\\d{8})$")
        return regex.matches(phone)
    }
}