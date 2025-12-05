package com.sako.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor untuk logging HTTP requests/responses
 * Membantu debugging integrasi backend
 */
class LoggingInterceptor : Interceptor {
    
    companion object {
        private const val TAG = "HTTP_REQUEST"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val startTime = System.nanoTime()

        // Log request
        Log.d(TAG, "🚀 Sending request: ${request.method} ${request.url}")
        request.headers.forEach { header ->
            Log.d(TAG, "Header: ${header.first} = ${header.second}")
        }

        try {
            val response: Response = chain.proceed(request)
            val endTime = System.nanoTime()
            val duration = (endTime - startTime) / 1_000_000 // Convert to milliseconds

            // Log response
            val responseCode = response.code
            val responseMessage = response.message
            
            when (responseCode) {
                in 200..299 -> {
                    Log.d(TAG, "✅ Response: $responseCode $responseMessage (${duration}ms)")
                }
                in 400..499 -> {
                    Log.w(TAG, "⚠️ Client Error: $responseCode $responseMessage (${duration}ms)")
                }
                in 500..599 -> {
                    Log.e(TAG, "❌ Server Error: $responseCode $responseMessage (${duration}ms)")
                }
                else -> {
                    Log.i(TAG, "ℹ️ Response: $responseCode $responseMessage (${duration}ms)")
                }
            }

            return response
        } catch (e: IOException) {
            val endTime = System.nanoTime()
            val duration = (endTime - startTime) / 1_000_000
            
            Log.e(TAG, "❌ Request failed: ${e.message} (${duration}ms)")
            throw e
        }
    }
}

/**
 * Extension functions untuk logging yang mudah digunakan
 */
fun String.logApiRequest(tag: String = "API_REQUEST") {
    Log.d(tag, "🔄 $this")
}

fun String.logApiResponse(tag: String = "API_RESPONSE", isSuccess: Boolean = true) {
    if (isSuccess) {
        Log.d(tag, "✅ $this")
    } else {
        Log.e(tag, "❌ $this")
    }
}

fun String.logApiError(tag: String = "API_ERROR") {
    Log.e(tag, "💥 $this")
}

/**
 * Helper untuk format response time
 */
fun Long.formatResponseTime(): String {
    return when {
        this < 100 -> "${this}ms (Sangat Cepat)"
        this < 300 -> "${this}ms (Cepat)"
        this < 1000 -> "${this}ms (Normal)"
        this < 3000 -> "${this}ms (Lambat)"
        else -> "${this}ms (Sangat Lambat)"
    }
}