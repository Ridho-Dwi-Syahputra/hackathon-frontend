package com.sako.firebase.notifications.video

import android.content.Context
import android.util.Log

/**
 * Video Notification Handler
 * Handles FCM notifications specifically for video module events
 */
object VideoNotificationHandler {
    
    private const val TAG = "VIDEO_NOTIFICATION_HANDLER"

    /**
     * Process FCM notification data for video module
     * Called by SakoFirebaseMessagingService when module="video"
     */
    fun processVideoNotification(
        context: Context,
        notificationData: Map<String, String>
    ): Boolean {
        try {
            val notificationType = notificationData["type"] ?: return false
            val videoTitle = notificationData["video_title"] ?: notificationData["title"] ?: "Unknown Video"
            val userName = notificationData["user_name"] ?: "Unknown User"
            
            Log.d(TAG, "🎬 Processing video notification: type=$notificationType, video=$videoTitle")
            
            when (notificationType) {
                "video_favorited" -> {
                    handleVideoFavoritedNotification(context, notificationData)
                }
                "video_uploaded" -> {
                    handleVideoUploadedNotification(context, notificationData)
                }
                else -> {
                    Log.w(TAG, "⚠️ Unknown video notification type: $notificationType")
                    return false
                }
            }
            
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error processing video notification: ${e.message}")
            return false
        }
    }

    /**
     * Handle video favorited notification
     */
    private fun handleVideoFavoritedNotification(
        context: Context,
        data: Map<String, String>
    ) {
        val videoTitle = data["video_title"] ?: data["title"] ?: "Unknown Video"
        val userName = data["user_name"] ?: "Unknown User"
        val videoId = data["video_id"] ?: ""
        
        Log.d(TAG, "⭐ Video favorited: $userName favorited video '$videoTitle' (ID: $videoId)")
        
        // Log untuk debugging backend integration
        Log.d(TAG, "Video favorite details - Video ID: $videoId")
    }

    /**
     * Handle video uploaded notification
     */
    private fun handleVideoUploadedNotification(
        context: Context,
        data: Map<String, String>
    ) {
        val videoTitle = data["video_title"] ?: data["title"] ?: "Unknown Video"
        val userName = data["user_name"] ?: "Unknown User"
        val videoId = data["video_id"] ?: ""
        val category = data["category"] ?: "Unknown Category"
        
        Log.d(TAG, "📤 Video uploaded: $userName uploaded '$videoTitle' in category $category")
        
        // Log untuk debugging backend integration
        Log.d(TAG, "Video upload details - Video ID: $videoId, Category: $category")
    }

    /**
     * Create notification title and body for video events
     */
    fun createNotificationContent(
        notificationType: String,
        data: Map<String, String>
    ): Pair<String, String> {
        val videoTitle = data["video_title"] ?: data["title"] ?: "Video"
        val userName = data["user_name"] ?: "Pengguna"
        
        return when (notificationType) {
            "video_favorited" -> {
                Pair(
                    "Video Ditambahkan ke Favorit",
                    "Video '$videoTitle' telah ditambahkan ke favorit"
                )
            }
            "video_uploaded" -> {
                val category = data["category"] ?: "kategori"
                Pair(
                    "Video Baru Tersedia",
                    "$userName mengunggah video baru '$videoTitle' di $category"
                )
            }
            else -> {
                Pair("Notifikasi Video", "Ada aktivitas baru di video")
            }
        }
    }

    /**
     * Get navigation intent data for video notifications
     */
    fun getNavigationData(
        notificationType: String,
        data: Map<String, String>
    ): Map<String, String> {
        return when (notificationType) {
            "video_favorited" -> mapOf(
                "screen" to "video_detail",
                "video_id" to (data["video_id"] ?: ""),
                "tab" to "favorites"
            )
            "video_uploaded" -> mapOf(
                "screen" to "video_detail",
                "video_id" to (data["video_id"] ?: ""),
                "category" to (data["category"] ?: "")
            )
            else -> mapOf("screen" to "video_home")
        }
    }
}
