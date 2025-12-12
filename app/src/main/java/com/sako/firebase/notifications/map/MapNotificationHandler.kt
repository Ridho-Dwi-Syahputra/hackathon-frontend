package com.sako.firebase.notifications.map

import android.content.Context
import android.util.Log
import com.sako.data.remote.response.ReviewResponse
import com.sako.data.remote.response.ScanQRResponse

/**
 * Map Notification Handler
 * Handles FCM notifications specifically for map module events
 */
object MapNotificationHandler {
    
    private const val TAG = "MAP_NOTIFICATION_HANDLER"

    /**
     * Process FCM notification data for map module
     * Called by SakoFirebaseMessagingService when module="map"
     */
    fun processMapNotification(
        context: Context,
        notificationData: Map<String, String>
    ): Boolean {
        try {
            val notificationType = notificationData["type"] ?: return false
            val placeName = notificationData["place_name"] ?: "Unknown Place"
            val userName = notificationData["user_name"] ?: "Unknown User"
            
            Log.d(TAG, "📍 Processing map notification: type=$notificationType, place=$placeName")
            
            when (notificationType) {
                "review_added" -> {
                    handleReviewAddedNotification(context, notificationData)
                }
                "place_visited" -> {
                    handlePlaceVisitedNotification(context, notificationData)
                }
                else -> {
                    Log.w(TAG, "⚠️ Unknown map notification type: $notificationType")
                    return false
                }
            }
            
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error processing map notification: ${e.message}")
            return false
        }
    }

    /**
     * Handle review added notification
     */
    private fun handleReviewAddedNotification(
        context: Context,
        data: Map<String, String>
    ) {
        val placeName = data["place_name"] ?: "Unknown Place"
        val userName = data["user_name"] ?: "Unknown User"
        val rating = data["rating"] ?: "0"
        val reviewId = data["review_id"] ?: ""
        
        Log.d(TAG, "⭐ Review notification: $userName rated $placeName ($rating stars)")
        
        // Log untuk debugging backend integration
        Log.d(TAG, "Review details - ID: $reviewId, Rating: $rating")
    }

    /**
     * Handle place visited notification
     */
    private fun handlePlaceVisitedNotification(
        context: Context,
        data: Map<String, String>
    ) {
        val placeName = data["place_name"] ?: "Unknown Place"
        val userName = data["user_name"] ?: "Unknown User"
        val qrCode = data["qr_code_value"] ?: ""
        val visitId = data["visit_id"] ?: ""
        
        Log.d(TAG, "🏛️ Visit notification: $userName visited $placeName")
        
        // Log untuk debugging backend integration
        Log.d(TAG, "Visit details - QR: $qrCode, Visit ID: $visitId")
    }

    /**
     * Create notification title and body for map events
     */
    fun createNotificationContent(
        notificationType: String,
        data: Map<String, String>
    ): Pair<String, String> {
        val placeName = data["place_name"] ?: "Tempat Wisata"
        val userName = data["user_name"] ?: "Pengguna"
        
        return when (notificationType) {
            "review_added" -> {
                val rating = data["rating"] ?: "5"
                Pair(
                    "Review Baru Ditambahkan",
                    "$userName menambahkan review $rating bintang untuk $placeName"
                )
            }
            "place_visited" -> {
                Pair(
                    "Kunjungan Tercatat",
                    "$userName telah mengunjungi $placeName"
                )
            }
            else -> {
                Pair("Notifikasi Map", "Ada aktivitas baru di peta")
            }
        }
    }

    /**
     * Get navigation intent data for map notifications
     */
    fun getNavigationData(
        notificationType: String,
        data: Map<String, String>
    ): Map<String, String> {
        return when (notificationType) {
            "review_added" -> mapOf(
                "screen" to "place_detail",
                "place_id" to (data["place_id"] ?: ""),
                "review_id" to (data["review_id"] ?: ""),
                "tab" to "reviews"
            )
            "place_visited" -> mapOf(
                "screen" to "place_detail", 
                "place_id" to (data["place_id"] ?: ""),
                "tab" to "info"
            )
            else -> mapOf("screen" to "map_home")
        }
    }
}