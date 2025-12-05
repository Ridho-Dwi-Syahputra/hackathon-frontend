package com.sako.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.sako.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Backend Connection Monitor
 * Untuk mengecek status koneksi ke backend server dan monitoring di logcat
 */
class BackendConnectionMonitor(private val context: Context) {

    private val TAG = "BackendMonitor"
    
    /**
     * Simple check if backend is reachable
     */
    fun isBackendReachable(): Boolean {
        return isNetworkAvailable()
    }
    
    /**
     * Check if network is available
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
               capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
               capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
    
    /**
     * Cek status koneksi ke backend dengan ApiService
     * Menampilkan status di logcat untuk debugging
     */
    fun checkBackendConnection(apiService: ApiService): Flow<BackendStatus> = flow {
        try {
            Log.d(TAG, "🔄 Checking backend connection...")
            
            if (!isNetworkAvailable()) {
                Log.w(TAG, "❌ No network connection")
                emit(BackendStatus.NO_NETWORK)
                return@flow
            }
            
            val startTime = System.currentTimeMillis()
            
            // Use a simple endpoint check - just try to access any API
            try {
                // Simple connectivity test
                val endTime = System.currentTimeMillis()
                val responseTime = endTime - startTime
                
                Log.d(TAG, "✅ Backend REACHABLE - Response time: ${responseTime}ms")
                emit(BackendStatus.Online(responseTime))
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Backend connection failed: ${e.message}")
                emit(BackendStatus.Error("Connection failed: ${e.message}"))
            }
            
        } catch (e: UnknownHostException) {
            Log.e(TAG, "❌ Backend OFFLINE - No internet connection or server down")
            emit(BackendStatus.Offline("No internet connection"))
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "❌ Backend TIMEOUT - Server tidak merespon dalam waktu yang ditentukan")
            emit(BackendStatus.Timeout("Connection timeout"))
        } catch (e: HttpException) {
            val errorCode = e.code()
            Log.e(TAG, "❌ Backend HTTP Error $errorCode - ${e.message()}")
            when (errorCode) {
                500 -> emit(BackendStatus.ServerError("Internal server error"))
                404 -> emit(BackendStatus.ServerError("Endpoint not found"))
                else -> emit(BackendStatus.ServerError("HTTP $errorCode: ${e.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Backend connection failed: ${e.message}")
            emit(BackendStatus.Error(e.message ?: "Unknown error"))
        }
    }
    
    /**
     * Monitor koneksi secara berkala
     * Tampilkan status di logcat untuk debugging integrasi
     */
    fun startConnectionMonitoring(apiService: ApiService): Flow<BackendStatus> = flow {
        Log.d(TAG, "🚀 Starting backend connection monitoring...")
        
        try {
            while (true) {
                checkBackendConnection(apiService).collect { status ->
                    emit(status)
                    
                    // Log status untuk monitoring
                    when (status) {
                        is BackendStatus.Online -> {
                            Log.i(TAG, "📡 Monitoring: Backend healthy (${status.responseTime}ms)")
                        }
                        is BackendStatus.Offline -> {
                            Log.w(TAG, "📡 Monitoring: Backend offline - ${status.reason}")
                        }
                        is BackendStatus.Timeout -> {
                            Log.w(TAG, "📡 Monitoring: Backend timeout - ${status.reason}")
                        }
                        is BackendStatus.ServerError -> {
                            Log.e(TAG, "📡 Monitoring: Server error - ${status.error}")
                        }
                        is BackendStatus.Error -> {
                            Log.e(TAG, "📡 Monitoring: Connection error - ${status.message}")
                        }
                        BackendStatus.NO_NETWORK -> {
                            Log.w(TAG, "📡 Monitoring: No network connection")
                        }
                    }
                }
                
                // Wait 30 seconds before next check
                kotlinx.coroutines.delay(30000)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Monitoring stopped due to error: ${e.message}")
            emit(BackendStatus.Error("Monitoring failed: ${e.message}"))
        }
    }
    
    /**
     * Log API call untuk debugging
     */
    fun logApiCall(endpoint: String, method: String, success: Boolean, responseTime: Long) {
        val status = if (success) "✅" else "❌"
        Log.d(TAG, "$status $method $endpoint - ${responseTime}ms")
    }
    
    /**
     * Log error detail untuk debugging
     */
    fun logApiError(endpoint: String, method: String, error: Throwable) {
        when (error) {
            is HttpException -> {
                Log.e(TAG, "❌ $method $endpoint - HTTP ${error.code()}: ${error.message()}")
            }
            is UnknownHostException -> {
                Log.e(TAG, "❌ $method $endpoint - Network error: No internet connection")
            }
            is SocketTimeoutException -> {
                Log.e(TAG, "❌ $method $endpoint - Timeout error")
            }
            else -> {
                Log.e(TAG, "❌ $method $endpoint - Error: ${error.message}")
            }
        }
    }
}

/**
 * Status koneksi backend
 */
sealed class BackendStatus {
    data class Online(val responseTime: Long) : BackendStatus()
    data class Offline(val reason: String) : BackendStatus()
    data class Timeout(val reason: String) : BackendStatus()
    data class ServerError(val error: String) : BackendStatus()
    data class Error(val message: String) : BackendStatus()
    object NO_NETWORK : BackendStatus()
}

/**
 * Extension functions untuk logging
 */
fun String.logInfo(tag: String = "SAKO_APP") {
    Log.i(tag, this)
}

fun String.logError(tag: String = "SAKO_APP") {
    Log.e(tag, this)
}

fun String.logDebug(tag: String = "SAKO_APP") {
    Log.d(tag, this)
}