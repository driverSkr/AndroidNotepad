package com.ethan.android.notepad.utils

import android.Manifest.permission
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.annotation.RequiresPermission
import com.ethan.android.notepad.NotepadApp
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NetWorkUtils {

    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }

    fun isConnectedGoogle(): Boolean {
        val command = "ping -c 1 google.com"
        return try {
            Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkNetWork() = suspendCoroutine {
        val connectivityManager = NotepadApp.INSTANCE?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (connectivityManager != null) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null) {
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.d("NetworkStatus", "Connected to WiFi")
                        it.resume(true)
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.d("NetworkStatus", "Connected to Cellular")
                        it.resume(true)
                    }
                    else {
                        it.resume(true)
                    }
                } else {
                    it.resume(false)
                }
            } else {
                it.resume(false)
                Log.d("NetworkStatus", "Not connected")
            }
        } else {
            it.resume(false)
        }
    }
}