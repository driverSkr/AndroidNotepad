package com.ethan.pay.utils

import android.content.Context

/**
 * Author: loren
 * Date: 2023/5/23
 */
object SubHelper {

    /**
     * ven 订阅商品id
     */
    private const val product_id_sub = "com_0117_sub"   //新版

    const val product_600 = "ven_0117_credits_600"
    const val product_1800 = "ven_0117_credits_1800"
    const val product_18000 = "ven_0117_credits_18000"

    /**
     * ven 订阅商品plan_id
     */
    private const val plan_id_week = "week-0117"
    private const val plan_id_year = "year-0117"

    /**
     * todo 请注意，如果添加了lifetime套餐，请向该list添加！！！，不然无法识别lifetime权益,以及积分包添加！！！！
     */
    val listLifeGoodsList = listOf<String>()

    fun getProductId(context: Context): String {
        return product_id_sub
    }

    fun getWeekPlanId(): String {
        return plan_id_week
    }

    fun getYearPlanId(): String {
        return plan_id_year
    }

    fun getWeekSkuId(context: Context): String {
        return product_id_sub
    }

    fun getYearSkuId(context: Context): String {
        return product_id_sub
    }
}