package com.sako.firebase.notifications.video

import android.content.Context
import android.util.Log
import com.sako.firebase.FirebaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Video Notification Manager
 * Coordinates video notification subscriptions and preferences
 */
class VideoNotificationManager private constructor(
    private val context: Context
) {
    
    private val preferencesManager = VideoNotificationPreferencesManager.getInstance(context)
    
    companion object {
        private const val TAG = "VIDEO_NOTIFICATION_MANAGER"
        
        // FCM Topics for video notifications
        private const val TOPIC_VIDEO_ALL = "video_notifications"
        private const val TOPIC_VIDEO_FAVORITED = "video_favorited_notifications"
        private const val TOPIC_VIDEO_UPLOADED = "video_uploaded_notifications"
        
        @Volatile
        private var INSTANCE: VideoNotificationManager? = null
        
        fun getInstance(context: Context): VideoNotificationManager {
            return INSTANCE ?: synchronized(this) {
                val instance = VideoNotificationManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Initialize video notifications on app start or user login
     */
    fun initializeVideoNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🚀 Initializing video notifications")
                
                // Log current preferences
                preferencesManager.logCurrentPreferences()
                
                // Subscribe to topics based on preferences
                if (preferencesManager.areVideoNotificationsEnabled()) {
                    subscribeToVideoTopics()
                } else {
                    Log.d(TAG, "🔕 Video notifications disabled, skipping subscription")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error initializing video notifications: ${e.message}")
            }
        }
    }
    
    /**
     * Subscribe to FCM topics for video notifications
     */
    private suspend fun subscribeToVideoTopics() {
        try {
            // Subscribe to general video topic
            FirebaseHelper.subscribeToTopic(TOPIC_VIDEO_ALL)
            Log.d(TAG, "✅ Subscribed to general video notifications")
            
            // Subscribe to specific topics based on preferences
            if (preferencesManager.areVideoFavoritedNotificationsEnabled()) {
                FirebaseHelper.subscribeToTopic(TOPIC_VIDEO_FAVORITED)
                Log.d(TAG, "✅ Subscribed to video favorited notifications")
            }
            
            if (preferencesManager.areVideoUploadedNotificationsEnabled()) {
                FirebaseHelper.subscribeToTopic(TOPIC_VIDEO_UPLOADED)
                Log.d(TAG, "✅ Subscribed to video uploaded notifications")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error subscribing to video topics: ${e.message}")
        }
    }
    
    /**
     * Unsubscribe from FCM topics for video notifications
     */
    private suspend fun unsubscribeFromVideoTopics() {
        try {
            FirebaseHelper.unsubscribeFromTopic(TOPIC_VIDEO_ALL)
            FirebaseHelper.unsubscribeFromTopic(TOPIC_VIDEO_FAVORITED)
            FirebaseHelper.unsubscribeFromTopic(TOPIC_VIDEO_UPLOADED)
            Log.d(TAG, "✅ Unsubscribed from all video topics")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error unsubscribing from video topics: ${e.message}")
        }
    }
    
    /**
     * Enable/disable video favorited notifications
     */
    fun setVideoFavoritedNotificationsEnabled(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            preferencesManager.setVideoFavoritedNotificationsEnabled(enabled)
            
            if (preferencesManager.areVideoNotificationsEnabled()) {
                if (enabled) {
                    FirebaseHelper.subscribeToTopic(TOPIC_VIDEO_FAVORITED)
                } else {
                    FirebaseHelper.unsubscribeFromTopic(TOPIC_VIDEO_FAVORITED)
                }
            }
        }
    }
    
    /**
     * Enable/disable video uploaded notifications
     */
    fun setVideoUploadedNotificationsEnabled(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            preferencesManager.setVideoUploadedNotificationsEnabled(enabled)
            
            if (preferencesManager.areVideoNotificationsEnabled()) {
                if (enabled) {
                    FirebaseHelper.subscribeToTopic(TOPIC_VIDEO_UPLOADED)
                } else {
                    FirebaseHelper.unsubscribeFromTopic(TOPIC_VIDEO_UPLOADED)
                }
            }
        }
    }
    
    /**
     * Enable/disable all video notifications
     */
    fun setVideoNotificationsEnabled(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "⚙️ Setting video notifications enabled: $enabled")
            preferencesManager.setVideoNotificationsEnabled(enabled)
            
            if (enabled) {
                subscribeToVideoTopics()
            } else {
                unsubscribeFromVideoTopics()
            }
            
            Log.d(TAG, "✅ Video notifications ${if (enabled) "enabled" else "disabled"} successfully")
        }
    }
    
    /**
     * Check if notification should be processed based on user preferences
     */
    fun shouldProcessNotification(notificationType: String): Boolean {
        val shouldProcess = preferencesManager.shouldShowNotification(notificationType)
        Log.d(TAG, "Checking if should process notification type '$notificationType': $shouldProcess")
        return shouldProcess
    }
    
    /**
     * Get current notification preferences for UI
     */
    fun getCurrentPreferences(): VideoNotificationPreferences {
        return VideoNotificationPreferences(
            allEnabled = preferencesManager.areVideoNotificationsEnabled(),
            favoritedEnabled = preferencesManager.areVideoFavoritedNotificationsEnabled(),
            uploadedEnabled = preferencesManager.areVideoUploadedNotificationsEnabled()
        )
    }
    
    /**
     * Update notification preferences and adjust topic subscriptions
     */
    fun updateNotificationPreferences(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "⚙️ Updating video notification preferences: enabled=$enabled")
                
                preferencesManager.setVideoNotificationsEnabled(enabled)
                
                if (enabled) {
                    subscribeToVideoTopics()
                } else {
                    unsubscribeFromVideoTopics()
                }
                
                Log.d(TAG, "✅ Video notification preferences updated successfully")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error updating video notification preferences: ${e.message}")
            }
        }
    }
}

/**
 * Data class for video notification preferences
 */
data class VideoNotificationPreferences(
    val allEnabled: Boolean,
    val favoritedEnabled: Boolean,
    val uploadedEnabled: Boolean
)
