package com.ethan.googlelogin

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


object GoogleLogin {

    private const val CLIENT_ID = "707719109957-uk2204cr2tg14bko5viovrnnbkj2ikfh.apps.googleusercontent.com"

    /**
     * 获取登录intent
     * 调起Google登录
     * 并监听登录结果及intent
     */
    fun signWithIntent(activity: Activity?): Intent? {
        return getClient(activity)?.signInIntent
    }

    /**
     * 在页面的onStart()中更新已登录账号信息
     */
    fun lastSignAccount(activity: Activity?, block: (account: GoogleAccount) -> Unit) {
        if (activity != null) {
            val googleAccount = GoogleAccount()
            googleAccount.createAccount(GoogleSignIn.getLastSignedInAccount(activity))
            if (!googleAccount.isEmpty()) {
                if (googleAccount.isExpired == true) {
                    refreshToken(activity, object : OnRefreshListener {
                        override fun onRefresh(account: GoogleAccount?) {
                            if (account != null && !account.isEmpty()) {
                                block(account)
                            }
                        }
                    })
                } else {
                    block(googleAccount)
                }
            }
        }
    }

    /**
     * 登出
     */
    fun signOut(activity: Activity?, listener: OnCompleteListener) {
        getClient(activity)?.signOut()?.addOnCompleteListener { listener.onComplete() }
    }

    /**
     * 处理登录回调结果，返回一个包装的account
     */
    fun handleAccount(activity: Activity?, data: Intent?, block: (account: GoogleAccount) -> Unit) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val signAccount = task.getResult(ApiException::class.java)
            val googleAccount = GoogleAccount()
            googleAccount.createAccount(signAccount)
            if (!googleAccount.isEmpty()) {
                if (googleAccount.isExpired == true) {
                    refreshToken(activity, object : OnRefreshListener {
                        override fun onRefresh(account: GoogleAccount?) {
                            if (account != null && !account.isEmpty()) {
                                block(account)
                            }
                        }
                    })
                } else {
                    block(googleAccount)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 刷新idToken
     */
    private fun refreshToken(activity: Activity?, listener: OnRefreshListener) {
        getClient(activity)?.silentSignIn()?.addOnCompleteListener { task ->
            try {
                val account = task.getResult(ApiException::class.java)
                val googleAccount = GoogleAccount()
                listener.onRefresh(googleAccount.createAccount(account))
            } catch (e: Exception) {
                e.printStackTrace()
                listener.onRefresh(null)
            }
        }
    }

    private fun getClient(activity: Activity?): GoogleSignInClient? {
        if (activity != null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID).requestEmail().build()
            return GoogleSignIn.getClient(activity, gso)
        }
        return null
    }
}