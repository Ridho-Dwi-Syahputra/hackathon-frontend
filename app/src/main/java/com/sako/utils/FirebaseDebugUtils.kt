package com.sako.utils

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.sako.firebase.FirebaseConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Firebase Debug Utils
 * Helper untuk debugging Firebase integration di logcat Android Studio
 */
object FirebaseDebugUtils {
    
    private const val TAG = "FIREBASE_DEBUG"

    /**
     * Log status Firebase untuk debugging
     */
    fun logFirebaseStatus(context: Context) {
        Log.d(TAG, "=== FIREBASE STATUS DEBUG ===")
        
        try {
            // Check if Firebase is initialized
            val firebaseApp = FirebaseApp.getInstance()
            Log.d(TAG, "✅ Firebase initialized")
            Log.d(TAG, "📱 App name: ${firebaseApp.name}")
            Log.d(TAG, "🆔 Project ID: ${firebaseApp.options.projectId}")
            Log.d(TAG, "📧 Client ID: ${firebaseApp.options.gcmSenderId}")
            
        } catch (e: IllegalStateException) {
            Log.e(TAG, "❌ Firebase not initialized: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase error: ${e.message}")
        }
        
        // Check FCM token availability
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fcmToken = FirebaseConfig.getFCMToken()
                if (fcmToken != null) {
                    Log.d(TAG, "🎯 FCM Token available: ${fcmToken.take(20)}...")
                } else {
                    Log.w(TAG, "⚠️ FCM Token not available")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ FCM Token error: ${e.message}")
            }
        }
        
        Log.d(TAG, "==============================")
    }

    /**
     * Log notification test untuk debugging
     */
    fun logNotificationTest(title: String, body: String, data: Map<String, String>) {
        Log.d(TAG, "=== NOTIFICATION TEST ===")
        Log.d(TAG, "📬 Title: $title")
        Log.d(TAG, "📝 Body: $body")
        Log.d(TAG, "📋 Data:")
        
        data.forEach { (key, value) ->
            Log.d(TAG, "   $key: $value")
        }
        
        Log.d(TAG, "========================")
    }

    /**
     * Log backend integration status
     */
    fun logBackendIntegration(
        isOnline: Boolean,
        lastResponseTime: Long?,
        fcmTokenSynced: Boolean
    ) {
        Log.d(TAG, "=== BACKEND INTEGRATION ===")
        Log.d(TAG, "🌐 Backend: ${if (isOnline) "✅ ONLINE" else "❌ OFFLINE"}")
        
        lastResponseTime?.let { responseTime ->
            Log.d(TAG, "⏱️ Response time: ${responseTime}ms")
        }
        
        Log.d(TAG, "🎯 FCM Token synced: ${if (fcmTokenSynced) "✅ YES" else "❌ NO"}")
        Log.d(TAG, "===========================")
    }

    /**
     * Log map notification activity
     */
    fun logMapNotificationActivity(
        action: String,
        placeName: String,
        userAction: String,
        success: Boolean
    ) {
        Log.d(TAG, "=== MAP NOTIFICATION ===")
        Log.d(TAG, "🗺️ Action: $action")
        Log.d(TAG, "📍 Place: $placeName")
        Log.d(TAG, "👤 User action: $userAction")
        Log.d(TAG, "📊 Status: ${if (success) "✅ SUCCESS" else "❌ FAILED"}")
        Log.d(TAG, "⏰ Time: ${System.currentTimeMillis()}")
        Log.d(TAG, "========================")
    }

    /**
     * Log complete integration flow
     */
    fun logIntegrationFlow(step: String, details: String, data: Any? = null) {
        Log.d(TAG, "🔄 INTEGRATION FLOW - $step")
        Log.d(TAG, "📝 Details: $details")
        
        data?.let {
            Log.d(TAG, "📋 Data: $it")
        }
        
        Log.d(TAG, "---")
    }

    /**
     * Test notification channels
     */
    fun testNotificationChannels(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            
            Log.d(TAG, "=== NOTIFICATION CHANNELS ===")
            
            val channels = notificationManager.notificationChannels
            if (channels.isNotEmpty()) {
                Log.d(TAG, "📱 Found ${channels.size} notification channels:")
                channels.forEach { channel ->
                    Log.d(TAG, "   🔔 ${channel.id}: ${channel.name} (importance: ${channel.importance})")
                }
            } else {
                Log.w(TAG, "⚠️ No notification channels found")
            }
            
            Log.d(TAG, "=============================")
        } else {
            Log.d(TAG, "📱 Android version < O, no notification channels")
        }
    }

    /**
     * Log Firebase configuration untuk debugging .env
     */
    fun logFirebaseConfig() {
        Log.d(TAG, "=== FIREBASE CONFIG ===")
        
        try {
            val firebaseApp = FirebaseApp.getInstance()
            val options = firebaseApp.options
            
            Log.d(TAG, "🆔 Project ID: ${options.projectId}")
            Log.d(TAG, "📧 GCM Sender ID: ${options.gcmSenderId}")
            Log.d(TAG, "🌐 Database URL: ${options.databaseUrl ?: "Not set"}")
            Log.d(TAG, "☁️ Storage Bucket: ${options.storageBucket ?: "Not set"}")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting Firebase config: ${e.message}")
        }
        
        Log.d(TAG, "======================")
    }
}