package com.ethan.sunny

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 网络变化监听器
 */
class ConnectionStateMonitor(val context: Context) : ConnectivityManager.NetworkCallback(),
    DefaultLifecycleObserver {
    companion object {
        private const val TAG = "ConnectionStateMonitor"
    }

    private var isRegister = false

    var networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    override fun onResume(owner: LifecycleOwner) {
        if (!isRegister) {
            Log.i(TAG, "ConnectionStateMonitor is resume")
            isRegister = true
            enable()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (isRegister) {
            Log.i(TAG, "ConnectionStateMonitor is OnDestroy")
            disable()
        }
    }

    /**
     * 绑定网络状态切换监听
     */
    fun enable() {
        Log.i(TAG, "Register")
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    /**
     * 解除绑定
     */
    fun disable() {
        try {
            Log.i(TAG, "Unregister")
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(this)
        } catch (e: Exception) {
            Log.i(TAG, "Unregister Exception Error : ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onAvailable(network: Network) {
        Log.i(TAG, "Network is Available")
        if (::networkAvailable.isInitialized) {
            networkAvailable(network)
        }
    }

    override fun onLost(network: Network) {
        Log.i(TAG, "Network is onLost")
        if (::networkLost.isInitialized) {
            networkLost(network)
        }
    }

    /**
     * 当有网络时的回调
     */
    private lateinit var networkAvailable: (Network) -> Unit

    /**
     * 无网络时的回调
     */
    private lateinit var networkLost: (network: Network?) -> Unit

    fun onConnectionAvailable(available: (network: Network) -> Unit) {
        this.networkAvailable = available
    }

    fun onConnectionLost(lost: (network: Network?) -> Unit) {
        this.networkLost = lost
    }

}