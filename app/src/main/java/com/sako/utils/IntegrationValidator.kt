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
 * Helper class untuk validasi integrasi backend
 * Berguna untuk testing dan debugging koneksi
 */
object IntegrationValidator {

    private const val TAG = "INTEGRATION_VALIDATOR"

    /**
     * Cek apakah perangkat memiliki koneksi internet
     */
    fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        val hasConnection = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        
        Log.d(TAG, "Internet connection: ${if (hasConnection) "✅ Available" else "❌ Not available"}")
        return hasConnection
    }

    /**
     * Test semua endpoint kritis untuk memastikan integrasi berfungsi
     */
    fun runIntegrationTest(apiService: ApiService): Flow<IntegrationTestResult> = flow {
        Log.d(TAG, "🧪 Starting integration test...")
        
        val testResults = mutableListOf<EndpointTestResult>()
        
        // Test 1: Categories endpoint (tidak perlu auth)
        testResults.add(testEndpoint("Categories", "GET /categories") {
            apiService.getCategories()
        })
        
        // Test 2: Auth endpoints akan ditest jika ada token
        // Test akan dilakukan oleh repository masing-masing
        
        emit(IntegrationTestResult(testResults, calculateOverallResult(testResults)))
    }

    private suspend fun <T> testEndpoint(
        name: String, 
        description: String, 
        test: suspend () -> T
    ): EndpointTestResult {
        return try {
            val startTime = System.currentTimeMillis()
            test()
            val endTime = System.currentTimeMillis()
            val responseTime = endTime - startTime
            
            Log.d(TAG, "✅ $name: SUCCESS (${responseTime}ms)")
            EndpointTestResult(name, description, true, responseTime, null)
            
        } catch (e: UnknownHostException) {
            Log.e(TAG, "❌ $name: Network error - ${e.message}")
            EndpointTestResult(name, description, false, 0, "Network error: No internet connection")
            
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "❌ $name: Timeout - ${e.message}")
            EndpointTestResult(name, description, false, 0, "Request timeout")
            
        } catch (e: HttpException) {
            Log.e(TAG, "❌ $name: HTTP ${e.code()} - ${e.message()}")
            EndpointTestResult(name, description, false, 0, "HTTP ${e.code()}: ${e.message()}")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ $name: Error - ${e.message}")
            EndpointTestResult(name, description, false, 0, e.message ?: "Unknown error")
        }
    }

    private fun calculateOverallResult(results: List<EndpointTestResult>): String {
        val successful = results.count { it.success }
        val total = results.size
        val percentage = (successful * 100) / total
        
        return when {
            percentage == 100 -> "✅ All tests passed ($successful/$total)"
            percentage >= 80 -> "⚠️ Most tests passed ($successful/$total)"
            percentage >= 50 -> "⚠️ Some tests failed ($successful/$total)"
            else -> "❌ Many tests failed ($successful/$total)"
        }
    }

    /**
     * Validasi format response sesuai dengan backend
     */
    fun validateResponseFormat(response: Any?): Boolean {
        // TODO: Implement response format validation based on backend documentation
        return response != null
    }

    /**
     * Log informasi sistem untuk debugging
     */
    fun logSystemInfo(context: Context) {
        Log.d(TAG, "=== SYSTEM INFO ===")
        Log.d(TAG, "App Package: ${context.packageName}")
        Log.d(TAG, "Internet: ${if (hasInternetConnection(context)) "Available" else "Not available"}")
        Log.d(TAG, "==================")
    }
}

/**
 * Data classes untuk hasil test integrasi
 */
data class IntegrationTestResult(
    val endpointResults: List<EndpointTestResult>,
    val overallResult: String
)

data class EndpointTestResult(
    val name: String,
    val description: String,
    val success: Boolean,
    val responseTime: Long,
    val error: String?
)