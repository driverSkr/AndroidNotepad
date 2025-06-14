package com.ethan.android.notepad.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ethan.android.notepad.BuildConfig
import com.ethan.company.NetConfig
import com.ethan.company.callback.SimpleCallback
import com.ethan.company.core.HttpExecutor
import com.ethan.company.gson.BaseResult
import com.ethan.android.notepad.ui.technique.model.SubInfoModel
import com.ethan.android.notepad.ui.technique.model.TokenModel
import com.ethan.android.notepad.ui.technique.model.UserModel
import com.ethan.android.notepad.utils.NetWorkUtils
import com.ethan.sunny.BaseViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserViewModel: BaseViewModel() {

    /**
     * 网络请求向后台获取token进行鉴权
     */
    suspend fun getToken(context: Context, path: String) = suspendCoroutine { suspendCoroutine ->
        Log.d("ethan_token", "调用者：$path")
        if (NetWorkUtils.isNetworkConnected(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                val paramList = mutableMapOf<String, Any>()
                paramList["package_name"] = context.packageName
                paramList["package_sign"] = "6a37dd59ff527bf435d464151040d37e"
                paramList["platform"] = "Android"
                Log.d("ethan_token", "package_name = ${paramList["package_name"]}")
                val server = HttpExecutor.getInstance().init(NetConfig.FUSION_AUTH, BuildConfig.DEBUG)

                HttpExecutor.getInstance()
                    .postString(server, NetConfig.FUSION_AUTH_GET_TOKEN, paramList, object : SimpleCallback {
                        override fun onSuccess(result: BaseResult?) {
                            if (result != null && result.isSuccess()) {
                                try {
                                    val gson = Gson()
                                    val json = gson.toJson(result.data)
                                    val token = gson.fromJson(json, TokenModel::class.java).tokenCode
                                    Log.d("ethan_token","鉴权token：$token")
                                    suspendCoroutine.resume(token)
                                } catch (e: Exception) {
                                    suspendCoroutine.resume(null)
                                    Log.d("ethan_token","Exception:鉴权token获取失败")
                                    e.printStackTrace()
                                }
                            } else {
                                Log.d("ethan_token","鉴权token获取失败, result = ${result.toString()}")
                                suspendCoroutine.resume(null)
                            }
                        }

                        override fun onFailure(code: Int, msg: String?) {
                            Log.d("ethan_token","onFailure：鉴权token获取失败")
                            suspendCoroutine.resume(null)
                        }
                    })
            }
        }
    }

    /**
     * 校验token
     * @param token 鉴权token成功后，阿里云返回的
     */
    suspend fun verifyToken(token: String?) = suspendCoroutine { suspendCoroutine ->
        viewModelScope.launch(Dispatchers.IO) {
            val tokenClear = removeNewlines(token)
            val paramList = mutableMapOf<String, Any>()
            paramList["token_code"] = tokenClear
            Log.d("ethan_token", "token_code = ${paramList["token_code"]}")
            val server = HttpExecutor.getInstance().init(NetConfig.FUSION_AUTH, BuildConfig.DEBUG)
            HttpExecutor.getInstance().postString(server, NetConfig.FUSION_AUTH_VERIFY_TOKEN, paramList, object : SimpleCallback {
                override fun onSuccess(result: BaseResult?) {
                    if (result != null && result.isSuccess()) {
                        try {
                            val gson = Gson()
                            val json = gson.toJson(result.data)
                            val myUserData = gson.fromJson(json, UserModel::class.java)
                            Log.d("ethan_token","myUserData = $myUserData")
                            suspendCoroutine.resume(myUserData)
                        } catch (e: Exception) {
                            Log.d("ethan_token","gson转换失败")
                            suspendCoroutine.resume(null)
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("ethan_token","未获得用户数据, result = ${result?.code} , ${result?.message}")
                        suspendCoroutine.resume(null)
                    }
                }
                override fun onFailure(code: Int, msg: String?) {
                    Log.d("ethan_token","http请求失败")
                    suspendCoroutine.resume(null)
                }
            })
        }
    }

    /**
     * 获取用户订阅信息
     * @param token 登录接口下发的token
     */
    suspend fun getUserSubscribeInfo(token: String?) = suspendCoroutine { suspendCoroutine ->
        if (token == null) {
            suspendCoroutine.resume(null)
            return@suspendCoroutine
        }
        viewModelScope.launch(Dispatchers.IO) {
            val paramList = mutableMapOf<String, Any>()
            val server = HttpExecutor.getInstance().init(NetConfig.FUSION_AUTH, BuildConfig.DEBUG)
            HttpExecutor.getInstance().addCommonHeaders("Authorization", token ?: "")
            val result = HttpExecutor.getInstance().asyncGetString(server, NetConfig.GET_USER_SUBSCRIBE_INFO, paramList)
            if (result != null && result.isSuccess()) {
                try {
                    val gson = Gson()
                    val json = gson.toJson(result.data)
                    val data = gson.fromJson(json, SubInfoModel::class.java)
                    data.activeTime = extractEndDate(data.createTime, data.activeTime)
                    Log.d("userViewModel","data = $data")
                    suspendCoroutine.resume(data)
                } catch (e: Exception) {
                    suspendCoroutine.resume(null)
                }
            } else {
                suspendCoroutine.resume(null)
            }
        }
    }

    private fun removeNewlines(input: String?): String {
        return input?.replace("\n", "") ?: ""
    }

    private fun extractEndDate(createTime: String?, activeTime: String?): String? {
        return activeTime?.replace("$createTime-", "")?.replace("-$createTime", "")
    }
}