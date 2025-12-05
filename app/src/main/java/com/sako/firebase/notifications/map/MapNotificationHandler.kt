package com.sako.firebase.notifications.map

import android.util.Log
import com.sako.data.remote.response.ReviewResponse
import com.sako.data.remote.response.ScanQRResponse
import com.sako.firebase.FirebaseConfig

/**
 * Map Notification Handler
 * Handles incoming notifications from backend for map module
 * Processes notification data dan triggers appropriate actions
 */
object MapNotificationHandler {
    
    private const val TAG = "MAP_NOTIFICATION_HANDLER"

    /**
     * Handle review added notification from backend
     * Triggered when user successfully adds a review
     */
    fun handleReviewAddedNotification(
        response: ReviewResponse,
        placeName: String,
        rating: Int
    ) {
        try {
            Log.d(TAG, "⭐ Processing review added notification")
            Log.d(TAG, "📍 Place: $placeName, Rating: $rating")
            
            if (response.success) {
                // Log successful review
                val message = "Ulasan berhasil ditambahkan untuk $placeName dengan rating $rating bintang"
                Log.i(TAG, message)
                
                // Notification akan dikirim otomatis dari backend via FCM
                // FCMService akan menangani notifikasi yang masuk
                
            } else {
                Log.w(TAG, "⚠️ Review failed, no notification sent")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error handling review notification: ${e.message}")
        }
    }

    /**
     * Handle place visited notification from backend
     * Triggered when user successfully scans QR code
     */
    fun handlePlaceVisitedNotification(
        response: ScanQRResponse,
        placeName: String
    ) {
        try {
            Log.d(TAG, "🏛️ Processing place visited notification")
            Log.d(TAG, "📍 Place: $placeName")
            
            if (response.success && response.data?.scan_success == true) {
                // Log successful visit
                val message = "Kunjungan berhasil dicatat untuk $placeName"
                Log.i(TAG, message)
                
                // Notification akan dikirim otomatis dari backend via FCM
                // FCMService akan menangani notifikasi yang masuk
                
                // Log visit info for debugging
                response.data?.let { scanData ->
                    Log.d(TAG, "✅ Visit recorded successfully")
                }
                
            } else {
                Log.w(TAG, "⚠️ QR scan failed, no notification sent")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error handling visit notification: ${e.message}")
        }
    }

    /**
     * Process notification data from FCM
     * Called by FCMService when notification is received
     */
    fun processNotificationData(data: Map<String, String>) {
        try {
            val notificationType = data["type"] ?: return
            val module = data["module"] ?: return
            
            if (module != "map") return
            
            Log.d(TAG, "📨 Processing FCM notification data")
            Log.d(TAG, "🔔 Type: $notificationType")
            
            when (notificationType) {
                "review_added" -> {
                    val placeName = data["place_name"] ?: "Unknown Place"
                    val rating = data["rating"]?.toIntOrNull() ?: 0
                    val userName = data["user_name"] ?: "Unknown User"
                    
                    Log.d(TAG, "⭐ Review notification: $userName reviewed $placeName ($rating stars)")
                }
                
                "place_visited" -> {
                    val placeName = data["place_name"] ?: "Unknown Place"
                    val userName = data["user_name"] ?: "Unknown User"
                    val qrCodeValue = data["qr_code_value"] ?: ""
                    
                    Log.d(TAG, "🏛️ Visit notification: $userName visited $placeName (QR: $qrCodeValue)")
                }
                
                else -> {
                    Log.d(TAG, "🔔 Unknown notification type: $notificationType")
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error processing notification data: ${e.message}")
        }
    }

    /**
     * Check notification preferences and filter accordingly
     */
    fun shouldShowNotification(
        notificationType: String,
        preferences: MapNotificationPreferences
    ): Boolean {
        return when (notificationType) {
            "review_added" -> preferences.reviewAdded
            "place_visited" -> preferences.placeVisited
            else -> true // Show unknown types by default
        }.also { shouldShow ->
            Log.d(TAG, "🔔 Should show $notificationType notification: $shouldShow")
        }
    }

    /**
     * Log notification activity for debugging
     */
    fun logNotificationActivity(
        action: String,
        notificationType: String,
        details: Map<String, Any> = emptyMap()
    ) {
        val timestamp = System.currentTimeMillis()
        val logMessage = "📊 Notification Activity: $action - $notificationType"
        
        Log.d(TAG, logMessage)
        Log.d(TAG, "⏰ Timestamp: $timestamp")
        
        details.forEach { (key, value) ->
            Log.d(TAG, "📋 $key: $value")
        }
    }
}