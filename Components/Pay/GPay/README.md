# GPay
谷歌支付

# 使用示例
package com.hitpaw.ven.ui.pay.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hitpaw.file.LogWriter
import com.hitpaw.firebaseAnalytics.analytics.Name
import com.hitpaw.network.NetConfig
import com.hitpaw.pay.BillFactory
import com.hitpaw.pay.model.Goods
import com.hitpaw.pay.model.OnPayResultCallback
import com.hitpaw.pay.model.OrderInfo
import com.hitpaw.pay.model.Receipt
import com.hitpaw.pay.utils.SubHelper
import com.hitpaw.ven.R
import com.hitpaw.ven.common.gson
import com.hitpaw.ven.common.utils.AESCrypto
import com.hitpaw.ven.common.utils.DataHelper
import com.hitpaw.ven.common.utils.ToastType
import com.hitpaw.ven.common.utils.showToast
import com.hitpaw.ven.config.Constants
import com.hitpaw.ven.event.Event
import com.hitpaw.ven.model.user.User
import com.hitpaw.ven.network.BaseViewModel
import com.hitpaw.ven.network.apiService
import com.hitpaw.ven.network.request
import com.hitpaw.ven.ui.pay.model.AdjustModel
import com.hitpaw.ven.ui.pay.model.ReportParams
import com.hitpaw.ven.ui.pay.model.SubModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MutableCollectionMutableState")
class PayViewModel : BaseViewModel() {

    var isBuySuccess: MutableState<Int> = mutableIntStateOf(0)
    var isBuyDiscordSuccess: MutableState<Int> = mutableIntStateOf(0)
    
    suspend fun querySubProduct(context: Context) = suspendCoroutine { suspendCoroutine ->
        viewModelScope.launch(Dispatchers.Default) {
            var isQueryPrice = false
            val goodsList = arrayListOf(SubHelper.getProductId(context), SubHelper.getProductId(context))
            val planList = arrayListOf(SubHelper.getYearPlanId(), SubHelper.getWeekPlanId())
            val offerList = arrayListOf("", "")
            val skuList = arrayListOf(SubHelper.getYearSkuId(context), SubHelper.getWeekSkuId(context))
            val list = mutableListOf<SubModel>()
            for (i in planList.indices) {
                val planId = planList[i]
                val model = SubModel()
                model.goods = goodsList[i]
                model.id = planId
                model.offerId = offerList[i]
                model.sku = skuList[i]
                val goods = Goods(goodsList[i], planId, offerList[i], skuList[i])
                val prices = BillFactory.getSubscribe().getGoodsPrice(context, goods)
                val trial = false
                if (prices[0] != null && prices[0] != "0.00") {
                    isQueryPrice = true
                }
                model.isFreeTrial = trial
                if (prices.size == 2) {
                    model.price = prices[0] ?: ""
                    model.currency = prices[1] ?: ""
                }
                list.add(model)
            }
            if (isQueryPrice) {
                Event.event(context, Name.Price_Search_Result, bundle = Bundle().apply {
                    putString("Label1", "success")
                })
                suspendCoroutine.resume(list)
            } else {
                Event.event(context, Name.Price_Search_Result, bundle = Bundle().apply {
                    putString("Label1", "failed")
                })
                suspendCoroutine.resume(null)
            }
        }
    }

    suspend fun queryPointProduct(context: Context) = suspendCoroutine { suspendCoroutine ->
        viewModelScope.launch(Dispatchers.Default) {
            var isQueryPrice = false
            val goodsList = arrayListOf(SubHelper.product_600, SubHelper.product_1800, SubHelper.product_18000)
            val planList = arrayListOf("", "", "")
            val offerList = arrayListOf("", "", "")
            val skuList = arrayListOf("", "", "")
            val list = mutableListOf<SubModel>()
            for (i in planList.indices) {
                val planId = planList[i]
                val model = SubModel()
                model.goods = goodsList[i]
                model.id = planId
                model.offerId = offerList[i]
                model.sku = skuList[i]
                val goods = Goods(goodsList[i], planId, offerList[i], skuList[i])
                val prices = BillFactory.getOneTime().getGoodsPrice(context, goods)
                val trial = false
                if (prices[0] != null && prices[0] != "0.00") {
                    isQueryPrice = true
                }
                model.isFreeTrial = trial
                if (prices.size == 2) {
                    model.price = prices[0] ?: ""
                    model.currency = prices[1] ?: ""
                }
                list.add(model)
            }
            if (isQueryPrice) {
                Event.event(context, Name.Price_Search_Result, bundle = Bundle().apply {
                    putString("Label1", "success")
                })
                suspendCoroutine.resume(list)
            } else {
                Event.event(context, Name.Price_Search_Result, bundle = Bundle().apply {
                    putString("Label1", "failed")
                })
                suspendCoroutine.resume(null)

            }
        }
    }

    fun buySubscribe(user: User, model: SubModel?, activity: FragmentActivity, dialog: MutableState<Boolean>, dialog2: MutableState<Boolean>) {
        viewModelScope.launch {
            val planId = model?.id.toString()
            val price = model?.price ?: ""
            val currency = model?.currency ?: ""
            LogWriter.append("launch InApp Billing $planId")
            val goods = Goods(model?.goods
                ?: SubHelper.getProductId(activity), planId, model?.offerId ?: "", model?.sku
                ?: SubHelper.getWeekSkuId(activity))
            withContext(Dispatchers.Main) {
                dialog.value = false
            }
            BillFactory.getSubscribe().launchBilling(activity, goods, object : OnPayResultCallback {
                override fun begin() {
                    LogWriter.append("InApp Billing begin")
                }

                override fun onSuccess(orderList: MutableList<OrderInfo>) {
                    report(activity, user, orderList, planId, price, currency, dialog2,model?.offerId)
                    DataHelper.VIP_Cache = true
                    LogWriter.append("InApp Billing success")
                }

                override fun onOwned(orderList: MutableList<OrderInfo>) {
                    report(activity, user, orderList, planId, price, currency, dialog2,model?.offerId)
                    DataHelper.VIP_Cache = true
                    LogWriter.append("InApp Billing owned")
                    // Event.event(activity, Name.Purchase_Owned, planId)
                }

                override fun onFailed(msg: String?) {
                    LogWriter.append("购买订阅失败")
                    viewModelScope.launch(Dispatchers.Main) {
                        R.string.purchase_failure.showToast(activity, ToastType.ERROR)
                    }
                    if (model?.offerId.isNullOrBlank())
                    {
                        isBuySuccess.value = 2
                    }
                    else
                    {
                        isBuyDiscordSuccess.value = 2
                    }

                    LogWriter.append("InApp Billing failure: $msg")
                }

                override fun onDisconnect() {
                    LogWriter.append("GooglePlay连接中断")
                    viewModelScope.launch(Dispatchers.Main) {
                        R.string.gp_connect_interrupted.showToast(activity, ToastType.ERROR)
                    }
                    if (model?.offerId.isNullOrBlank())
                    {
                        isBuySuccess.value = 3
                    }
                    else
                    {
                        isBuyDiscordSuccess.value = 3
                    }
                    LogWriter.append("InApp Billing disconnect")
                }

                override fun onCancel() {
                    Log.d("Ethan", "购买订阅取消")
                    if (model?.offerId.isNullOrBlank())
                    {
                        isBuySuccess.value = 4
                    }
                    else
                    {
                        isBuyDiscordSuccess.value = 4
                    }
                    viewModelScope.launch(Dispatchers.Main) {
                        R.string.purchase_cancel.showToast(activity, ToastType.HINT)
                    }
                    LogWriter.append("InApp Billing cancel")
                }
            })
        }
    }

    suspend fun buyPointGoods(user: User, model: SubModel, activity: FragmentActivity, dialog: MutableState<Boolean>, dialog2: MutableState<Boolean>) =
        suspendCoroutine {
            val TAG = "buyPointGoods"
            val goods = Goods(model.goods ?: "", "", "", "")
            val callback = object : OnPayResultCallback {

                private fun result(param: Pair<Boolean, MutableList<OrderInfo>?>) {
                    try {
                        it.resume(param)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                private suspend fun notify(orderList: MutableList<OrderInfo>) {
                    val order = orderList.firstOrNull()
                    Log.i(TAG, "initGlobalSuccessCallback: 购买成功，notify  point")
                    LogWriter.append("buy point succeed ${order?.goodsId} ${order?.token}")
                }

                override fun begin() {
                    dialog.value = false
                }

                override fun onSuccess(orderList: MutableList<OrderInfo>) {
                    viewModelScope.launch(Dispatchers.Default) {
                        notify(orderList)
                        report(activity, user, orderList, model.goods ?: "", model.price
                            ?: "", model.currency ?: "", dialog2)
                        result(true to orderList)
                    }
                }


                override fun onOwned(orderList: MutableList<OrderInfo>) {
                    viewModelScope.launch(Dispatchers.Default) {
                        notify(orderList)
                        report(activity, user, orderList, model.goods ?: "", model.price
                            ?: "", model.currency ?: "", dialog2)
                        result(true to orderList)
                    }
                }

                override fun onFailed(msg: String?) {
                    LogWriter.append("积分购买失败")
                    viewModelScope.launch(Dispatchers.Main) {
                        R.string.purchase_failure.showToast(activity, ToastType.ERROR)
                    }
                    if (model.goods==SubHelper.product_300)
                    {
                        isBuyDiscordSuccess.value = 2
                    }
                    else
                    {
                        isBuySuccess.value = 2
                    }
                    result(false to null)
                }

                override fun onDisconnect() {
                    LogWriter.append("GooglePlay连接中断")
                    viewModelScope.launch(Dispatchers.Main) {
                        R.string.gp_connect_interrupted.showToast(activity, ToastType.ERROR)
                    }
                    if (model.goods==SubHelper.product_300)
                    {
                        isBuyDiscordSuccess.value = 3
                    }
                    else
                    {
                        isBuySuccess.value = 3
                    }

                    result(false to null)
                }

                override fun onCancel() {
                    LogWriter.append("积分购买取消")
                    viewModelScope.launch(Dispatchers.Main) {
                        R.string.purchase_cancel.showToast(activity, ToastType.HINT)
                    }
                    if (model.goods==SubHelper.product_300)
                    {
                        isBuyDiscordSuccess.value = 4
                    }
                    else
                    {
                        isBuySuccess.value = 4
                    }
                    result(false to null)
                }

            }
            viewModelScope.launch(Dispatchers.Default) {
                Log.i(TAG, "buyPointGoods: 购买 OneTime ${model.goods}")
                try {
                    BillFactory.getOneTime().launchBilling(activity, goods, callback)
                } catch (e: Exception) {
                    Log.e(TAG, "支付失败", e)
                } catch (t: Throwable) {
                    Log.e(TAG, "支付系统级错误", t)
                }
            }
        }




    @DelicateCoroutinesApi
    fun report(context: Context, user: User, orderList: MutableList<OrderInfo>, planId: String, price: String, currency: String, dialog: MutableState<Boolean>, offid:String?=null) {
        dialog.value = true
        viewModelScope.launch(Dispatchers.Default) {
            orderList.forEach { orderInfo ->
                LogWriter.append("订单-》 ${AESCrypto.encrypt(orderInfo.orderId ?: "")}")
                val json = orderInfo.json?.trimEnd()?.replace("\\", "")
                val receiptJson = gson.toJson(Receipt(json, orderInfo.signature))?.replace("\\", "")
                val receipt = receiptJson?.replace("{\"receipt\":\"{", "{\"receipt\":{")?.replace("}\",\"signature", "},\"signature")?.replace("\\", "")

                val jsonObject = JsonObject()
                jsonObject.addProperty("is_bonus", if (productType(planId)) 1 else 0)

                val getReportPram = ReportParams(
                    orderInfo.orderId ?: "",
                    Constants.PID.toString(),
                    mapTable(planId) ?: "",
                    price,
                    currency,
                    DataHelper.getUserUnique(context),
                    Base64.encodeToString(receipt?.toByteArray(Charsets.UTF_8), Base64.DEFAULT).replace("\n", ""),
                    adjust_param = gson.toJson(getAdjustModel(context)),
                    order_param = gson.toJson(jsonObject)
                )
                val req = Gson()
                    .toJson(getReportPram).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                request({ apiService.subReport(user.authorization_token ?: "", req) }, { myData ->
                    dialog.value = false
                    if (myData.pay_status == 200) {
                        LogWriter.append("上报成功!")
                        println("上报成功!!")
                        if (planId==SubHelper.product_300)
                        {
                            isBuyDiscordSuccess.value = 1
                        }
                        else
                        {
                            if (offid.isNullOrBlank())
                            {
                                isBuySuccess.value = 1
                            }
                            else
                            {
                                isBuyDiscordSuccess.value = 1
                            }

                        }

                        R.string.purchase_succeed.showToast(context, ToastType.SUCCESS)
                    } else {
                        R.string.purchase_failure.showToast(context, ToastType.ERROR)
                    }
                    println("后台返回的数据：$myData")
                }, {
                    dialog.value = false
                    println(it.errorMessage)
                    if (planId==SubHelper.product_300)
                    {
                        isBuyDiscordSuccess.value = 2
                    }
                    else
                    {
                        if (offid.isNullOrBlank())
                        {
                            isBuySuccess.value = 2
                        }
                        else
                        {
                            isBuyDiscordSuccess.value = 2
                        }

                    }
                    R.string.network_error_long.showToast(context, ToastType.ERROR)
                })
            }
        }
    }

    private fun getAdjustModel(context: Context): AdjustModel {
        val adjustModel = AdjustModel()
        adjustModel.gps_adid = getAdsId(context)
        return adjustModel
    }

    @WorkerThread
    fun getAdsId(context: Context): String? {
        return try {
            AdvertisingIdClient.getAdvertisingIdInfo(context).id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun mapTable(planId: String): String? {
        val map = mutableMapOf<String, String>()
        map[SubHelper.getWeekPlanId()] = NetConfig.WEEK
        map[SubHelper.getYearPlanId()] = NetConfig.YEAR
        map[SubHelper.product_300] = NetConfig.CBS_ID_POINTS_300
        map[SubHelper.product_600] = NetConfig.CBS_ID_POINTS_600
        map[SubHelper.product_1800] = NetConfig.CBS_ID_POINTS_1800
        map[SubHelper.product_18000] = NetConfig.CBS_ID_POINTS_18000
        return if (map.containsKey(planId)) map[planId] else null
    }

    private fun productType(planId: String): Boolean {
        val list = mutableListOf<String>()
        list.add(SubHelper.product_300)
        list.add(SubHelper.product_600)
        list.add(SubHelper.product_1800)
        list.add(SubHelper.product_18000)
        return list.contains(planId)
    }
}