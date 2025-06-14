package com.ethan.pay.model

/**
 * Author: loren
 * Date: 2022/6/24
 */
enum class PurchaseState(val value: Int) {
    GPAY(1), DISCONNECT(2), NOTPAY(3)
}