package com.ethan.android.notepad.utils

import android.content.Context
import com.ethan.android.notepad.ui.technique.model.UserModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataHelper {

    fun setLanguage(context: Context, language: String) {
        val sharedPreferences = context.getSharedPreferences("sp_language", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("sp_language_data", language)
        editor.apply()
    }

    fun getLanguage(context: Context) : String? {
        val sharedPreferences = context.getSharedPreferences("sp_language", Context.MODE_PRIVATE)
        return sharedPreferences.getString("sp_language_data", null)
    }

    fun setUserData(context: Context, userData: UserModel?) {
//        AIPortraitAPP.INSTANCE?.userData = userData
//        val sharedPreferences =
//            context.getSharedPreferences(Constants.CATHCE_NAME, Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        if (userData == null) {
//            editor.putString(Constants.USER_DATA_NAME, "")
//        } else {
//            val str = Gson().toJson(userData)
//            editor.putString(Constants.USER_DATA_NAME, str)
//        }
//        editor.apply()
    }

    fun getUserData(context: Context): UserModel? {
        val sharedPreferences =
            context.getSharedPreferences("", Context.MODE_PRIVATE)
        val str = sharedPreferences?.getString("", "")
        return if (str == "") {
            null
        } else {
            Gson().fromJson(str, object : TypeToken<UserModel>() {}.type)
        }
    }

    //是否同意启动的权限访问提示弹窗
    fun isAgreeInitiatedPermissionDialog(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("sp_initiated_permission_dialog", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_select", "no_select")
    }

    //设置用户启动的权限访问提示弹窗的选择
    fun setAgreeInitiatedPermissionDialog(context: Context, userSelect: String) {
        val sharedPreferences = context.getSharedPreferences("sp_initiated_permission_dialog", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_select", userSelect)
        editor.apply()
    }

    //是否同意启动的权限访问提示弹窗
    fun isAgreeRequestImagePermissionDialog(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("sp_request_image_permission_dialog", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_select", "no_select")
    }

    //设置用户启动的权限访问提示弹窗的选择
    fun setAgreeRequestImagePermissionDialog(context: Context, userSelect: String) {
        val sharedPreferences = context.getSharedPreferences("sp_request_image_permission_dialog", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_select", userSelect)
        editor.apply()
    }
}