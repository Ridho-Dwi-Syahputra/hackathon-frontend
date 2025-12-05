package com.sako.firebase

import android.util.Log

/**
 * Firebase Debug Utilities
 * Centralized logging untuk Firebase operations
 */
object FirebaseDebugUtils {
    
    private const val TAG = "SAKO_FIREBASE"
    
    /**
     * Log informational messages
     */
    fun logInfo(message: String) {
        Log.i(TAG, "ℹ️ $message")
    }
    
    /**
     * Log debug messages
     */
    fun logDebug(message: String) {
        Log.d(TAG, "🔍 $message")
    }
    
    /**
     * Log warning messages
     */
    fun logWarning(message: String) {
        Log.w(TAG, "⚠️ $message")
    }
    
    /**
     * Log error messages
     */
    fun logError(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, "❌ $message", throwable)
        } else {
            Log.e(TAG, "❌ $message")
        }
    }
    
    /**
     * Log FCM token for debugging
     */
    fun logFCMToken(token: String?) {
        if (token != null) {
            logInfo("FCM Token: ${token.take(20)}...${token.takeLast(10)}")
        } else {
            logError("FCM Token is null")
        }
    }
    
    /**
     * Log notification data
     */
    fun logNotificationData(data: Map<String, String>) {
        logDebug("Notification data received:")
        data.forEach { (key, value) ->
            logDebug("  $key: $value")
        }
    }
}