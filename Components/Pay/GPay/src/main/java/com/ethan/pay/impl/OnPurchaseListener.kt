package com.ethan.pay.impl

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase

/**
 * Author: loren
 * Date: 2023/1/5
 */
interface OnPurchaseListener {
    fun onPurchase(result: BillingResult, purchases: List<Purchase>?)
}