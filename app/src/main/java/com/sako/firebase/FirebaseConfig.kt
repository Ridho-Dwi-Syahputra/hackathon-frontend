package com.sako.firebase

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Firebase Configuration and Token Management
 * Manages FCM token generation and updates
 */
object FirebaseConfig {
    
    private const val TAG = "FIREBASE_CONFIG"

    /**
     * Initialize Firebase for the app
     */
    fun initialize(context: Context) {
        try {
            FirebaseApp.initializeApp(context)
            Log.d(TAG, "✅ Firebase berhasil diinisialisasi")
            
            // Subscribe to map notifications topic
            subscribeToMapNotifications()
        } catch (e: Exception) {
            Log.e(TAG, "❌ Gagal inisialisasi Firebase: ${e.message}")
        }
    }

    /**
     * Get FCM token for current installation
     */
    suspend fun getFCMToken(): String? = suspendCancellableCoroutine { continuation ->
        try {
            // Check if Firebase is properly initialized first
            if (!isInitialized()) {
                Log.w(TAG, "⚠️ Firebase app not initialized, returning dummy token")
                continuation.resume("dummy_fcm_token_${System.currentTimeMillis()}")
                return@suspendCancellableCoroutine
            }
            
            // Check if google-services.json has valid API key
            val firebaseApp = FirebaseApp.getInstance()
            val apiKey = firebaseApp?.options?.apiKey
            if (apiKey.isNullOrEmpty() || apiKey.contains("dummy", ignoreCase = true)) {
                Log.w(TAG, "⚠️ Firebase API key not properly configured, returning dummy token")
                continuation.resume("dummy_fcm_token_${System.currentTimeMillis()}")
                return@suspendCancellableCoroutine
            }
            
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "❌ Gagal mendapatkan FCM token: ${task.exception?.message}")
                    // Return dummy token for development instead of null
                    continuation.resume("dummy_fcm_token_${System.currentTimeMillis()}")
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d(TAG, "🎯 FCM Token: $token")
                continuation.resume(token)
            })
        } catch (e: Exception) {
            Log.w(TAG, "❌ Gagal mendapatkan FCM token (development mode)")
            e.printStackTrace()
            // Return dummy token for development
            continuation.resume("dummy_fcm_token_${System.currentTimeMillis()}")
        }
    }

    /**
     * Subscribe to map notifications topic
     */
    fun subscribeToMapNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("map_notifications")
            .addOnCompleteListener { task ->
                var msg = "✅ Berhasil subscribe ke map notifications"
                if (!task.isSuccessful) {
                    msg = "❌ Gagal subscribe ke map notifications"
                }
                Log.d(TAG, msg)
            }
    }

    /**
     * Unsubscribe from map notifications (when user disables notifications)
     */
    fun unsubscribeFromMapNotifications() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("map_notifications")
            .addOnCompleteListener { task ->
                var msg = "✅ Berhasil unsubscribe dari map notifications"
                if (!task.isSuccessful) {
                    msg = "❌ Gagal unsubscribe dari map notifications"
                }
                Log.d(TAG, msg)
            }
    }

    /**
     * Delete FCM token (when user logout)
     */
    suspend fun deleteFCMToken(): Boolean = suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "✅ FCM token berhasil dihapus")
                continuation.resume(true)
            } else {
                Log.e(TAG, "❌ Gagal hapus FCM token: ${task.exception}")
                continuation.resume(false)
            }
        }
    }

    /**
     * Check if Firebase is initialized
     */
    fun isInitialized(): Boolean {
        return try {
            FirebaseApp.getInstance()
            true
        } catch (e: IllegalStateException) {
            false
        }
    }
}