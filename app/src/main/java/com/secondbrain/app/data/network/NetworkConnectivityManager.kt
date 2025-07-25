package com.secondbrain.app.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Manages network connectivity state and provides a flow of connectivity status.
 */
class NetworkConnectivityManager(context: Context) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    /**
     * Flow that emits true when the device is connected to a network, false otherwise.
     */
    val isConnected: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                launch { send(true) }
            }
            
            override fun onLost(network: Network) {
                super.onLost(network)
                launch { send(false) }
            }
            
            override fun onUnavailable() {
                super.onUnavailable()
                launch { send(false) }
            }
        }
        
        // Register the callback
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
            
        connectivityManager.registerNetworkCallback(request, callback)
        
        // Initial state
        launch { 
            send(checkConnected()) 
        }
        
        // Unregister the callback when the flow is cancelled
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
    
    /**
     * Checks if the device is currently connected to a network.
     */
    fun isConnected(): Boolean {
        return checkConnected()
    }
    
    /**
     * Checks if the device is currently connected to a network (alternative method name).
     */
    fun checkConnection(): Boolean {
        return checkConnected()
    }
    
    private fun checkConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
