package com.ethan.android.notepad.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.ethan.android.notepad.NotepadApp
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NetWorkHelper {

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
                    } else {
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