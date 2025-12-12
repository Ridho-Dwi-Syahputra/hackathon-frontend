package com.sako.firebase

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Firebase Helper untuk mengelola semua fungsi Firebase
 * Dibuat terpisah untuk memudahkan konfigurasi dan debugging
 * 
 * @author SAKO Development Team
 * @version 2.0.0
 */
object FirebaseHelper {
    
    private const val TAG = "FIREBASE_HELPER"
    private var isInitialized = false
    
    /**
     * Initialize Firebase dengan konfigurasi yang benar
     */
    fun initialize(context: Context) {
        try {
            if (!isInitialized) {
                // Initialize Firebase App
                val app = FirebaseApp.initializeApp(context)
                
                if (app != null) {
                    Log.d(TAG, "✅ Firebase berhasil diinisialisasi")
                    Log.d(TAG, "📱 App ID: ${app.options.applicationId}")
                    Log.d(TAG, "🔑 API Key: ${app.options.apiKey.take(20)}...")
                    Log.d(TAG, "📁 Project ID: ${app.options.projectId}")
                    
                    // Initialize Analytics
                    FirebaseAnalytics.getInstance(context)
                    Log.d(TAG, "📊 Firebase Analytics initialized")
                    
                    // Subscribe to default topic
                    subscribeToTopic("sako_notifications")
                    
                    isInitialized = true
                } else {
                    Log.e(TAG, "❌ FirebaseApp.initializeApp returned null")
                }
            } else {
                Log.d(TAG, "ℹ️ Firebase sudah diinisialisasi sebelumnya")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Gagal inisialisasi Firebase: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Generate FCM Token dengan error handling yang lebih baik
     */
    suspend fun generateFCMToken(): String? = suspendCancellableCoroutine { continuation ->
        try {
            if (!isFirebaseReady()) {
                Log.w(TAG, "⚠️ Firebase belum ready, returning dummy token")
                continuation.resume("dummy_fcm_token_${System.currentTimeMillis()}")
                return@suspendCancellableCoroutine
            }
            
            Log.d(TAG, "🔄 Generating FCM token...")
            
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "❌ Gagal mendapatkan FCM token: ${task.exception?.message}")
                        task.exception?.printStackTrace()
                        // Return dummy token for development
                        continuation.resume("dummy_fcm_token_${System.currentTimeMillis()}")
                        return@addOnCompleteListener
                    }
                    
                    val token = task.result
                    Log.d(TAG, "✅ FCM Token berhasil: ${token.take(50)}...")
                    continuation.resume(token)
                }
                
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception saat generate FCM token: ${e.message}")
            e.printStackTrace()
            continuation.resume("dummy_fcm_token_${System.currentTimeMillis()}")
        }
    }
    
    /**
     * Subscribe ke topic untuk notifikasi
     */
    fun subscribeToTopic(topic: String) {
        try {
            if (!isFirebaseReady()) {
                Log.w(TAG, "⚠️ Firebase belum ready, skip subscribe to topic")
                return
            }
            
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "✅ Berhasil subscribe ke topic: $topic")
                    } else {
                        Log.w(TAG, "❌ Gagal subscribe ke topic: $topic, error: ${task.exception?.message}")
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception saat subscribe topic: ${e.message}")
        }
    }
    
    /**
     * Unsubscribe dari topic
     */
    fun unsubscribeFromTopic(topic: String) {
        try {
            if (!isFirebaseReady()) {
                Log.w(TAG, "⚠️ Firebase belum ready, skip unsubscribe from topic")
                return
            }
            
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "✅ Berhasil unsubscribe dari topic: $topic")
                    } else {
                        Log.w(TAG, "❌ Gagal unsubscribe dari topic: $topic, error: ${task.exception?.message}")
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception saat unsubscribe topic: ${e.message}")
        }
    }
    
    /**
     * Delete FCM token (saat logout)
     */
    suspend fun deleteFCMToken(): Boolean = suspendCancellableCoroutine { continuation ->
        try {
            if (!isFirebaseReady()) {
                Log.w(TAG, "⚠️ Firebase belum ready, return true for dummy delete")
                continuation.resume(true)
                return@suspendCancellableCoroutine
            }
            
            FirebaseMessaging.getInstance().deleteToken()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "✅ FCM token berhasil dihapus")
                        continuation.resume(true)
                    } else {
                        Log.e(TAG, "❌ Gagal hapus FCM token: ${task.exception?.message}")
                        continuation.resume(false)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception saat delete FCM token: ${e.message}")
            continuation.resume(false)
        }
    }
    
    /**
     * Cek apakah Firebase sudah ready untuk digunakan
     */
    private fun isFirebaseReady(): Boolean {
        return try {
            val app = FirebaseApp.getInstance()
            val options = app.options
            
            val hasValidApiKey = !options.apiKey.isNullOrEmpty() && !options.apiKey.contains("dummy", ignoreCase = true)
            val hasValidAppId = !options.applicationId.isNullOrEmpty()
            val hasValidProjectId = !options.projectId.isNullOrEmpty()
            
            val isReady = hasValidApiKey && hasValidAppId && hasValidProjectId
            
            Log.d(TAG, "🔍 Firebase Ready Check:")
            Log.d(TAG, "  - API Key valid: $hasValidApiKey")
            Log.d(TAG, "  - App ID valid: $hasValidAppId") 
            Log.d(TAG, "  - Project ID valid: $hasValidProjectId")
            Log.d(TAG, "  - Overall ready: $isReady")
            
            isReady
        } catch (e: Exception) {
            Log.w(TAG, "❌ Firebase ready check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Get Firebase configuration info untuk debugging
     */
    fun getConfigInfo(): Map<String, String> {
        return try {
            val app = FirebaseApp.getInstance()
            val options = app.options
            
            mapOf(
                "apiKey" to (options.apiKey?.take(20) + "..." ?: "null"),
                "applicationId" to (options.applicationId ?: "null"),
                "projectId" to (options.projectId ?: "null"),
                "storageBucket" to (options.storageBucket ?: "null"),
                "isReady" to isFirebaseReady().toString()
            )
        } catch (e: Exception) {
            mapOf("error" to e.message.toString())
        }
    }
}