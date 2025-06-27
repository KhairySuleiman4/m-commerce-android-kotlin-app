package com.example.mcommerce.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConnectivityObserver(context: Context) {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val callback = object : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val connected = networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
            _isConnected.value = connected
        }

        override fun onUnavailable() {
            super.onUnavailable()
            _isConnected.value = false
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _isConnected.value = false
        }
    }

    fun unregisterCallBack() {
        connectivityManager.unregisterNetworkCallback(callback)
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(callback)
    }
}