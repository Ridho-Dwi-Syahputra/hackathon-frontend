package com.sako.firebase.notifications.video

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Video Notification Preferences Manager
 * Manages user preferences for video module notifications
 */
class VideoNotificationPreferencesManager private constructor(
    private val context: Context
) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )
    
    companion object {
        private const val TAG = "VIDEO_NOTIFICATION_PREFS"
        private const val PREF_NAME = "video_notification_preferences"
        
        // Preference keys
        private const val KEY_VIDEO_NOTIFICATIONS = "video_notifications_enabled"
        private const val KEY_VIDEO_FAVORITED = "video_favorited_enabled"
        private const val KEY_VIDEO_UPLOADED = "video_uploaded_enabled"
        
        @Volatile
        private var INSTANCE: VideoNotificationPreferencesManager? = null
        
        fun getInstance(context: Context): VideoNotificationPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = VideoNotificationPreferencesManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Check if all video notifications are enabled
     */
    fun areVideoNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VIDEO_NOTIFICATIONS, true)
    }
    
    /**
     * Check if video favorited notifications are enabled
     */
    fun areVideoFavoritedNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VIDEO_FAVORITED, true)
    }
    
    /**
     * Check if video uploaded notifications are enabled  
     */
    fun areVideoUploadedNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VIDEO_UPLOADED, true)
    }
    
    /**
     * Set all video notifications enabled/disabled
     */
    fun setVideoNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_VIDEO_NOTIFICATIONS, enabled)
            .apply()
        Log.d(TAG, "All video notifications ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Set video favorited notifications enabled/disabled
     */
    fun setVideoFavoritedNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_VIDEO_FAVORITED, enabled)
            .apply()
        Log.d(TAG, "Video favorited notifications ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Set video uploaded notifications enabled/disabled
     */
    fun setVideoUploadedNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_VIDEO_UPLOADED, enabled)
            .apply()
        Log.d(TAG, "Video uploaded notifications ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Check if specific notification type should be shown
     */
    fun shouldShowNotification(notificationType: String): Boolean {
        // First check if all video notifications are enabled
        if (!areVideoNotificationsEnabled()) {
            Log.d(TAG, "🔕 Video notifications disabled globally for type: $notificationType")
            return false
        }
        
        // Then check specific notification type
        return when (notificationType) {
            "video_favorited" -> {
                val enabled = areVideoFavoritedNotificationsEnabled()
                Log.d(TAG, "Video favorited notification check: $enabled")
                enabled
            }
            "video_uploaded" -> {
                val enabled = areVideoUploadedNotificationsEnabled()
                Log.d(TAG, "Video uploaded notification check: $enabled")
                enabled
            }
            else -> {
                Log.d(TAG, "Unknown video notification type, allowing by default: $notificationType")
                true // Show unknown types by default if video notifications are enabled
            }
        }
    }
    
    /**
     * Reset all preferences to default (all enabled)
     */
    fun resetToDefault() {
        sharedPreferences.edit()
            .putBoolean(KEY_VIDEO_NOTIFICATIONS, true)
            .putBoolean(KEY_VIDEO_FAVORITED, true)
            .putBoolean(KEY_VIDEO_UPLOADED, true)
            .apply()
        Log.d(TAG, "Video notification preferences reset to default")
    }
    
    /**
     * Log current preferences for debugging
     */
    fun logCurrentPreferences() {
        Log.d(TAG, "=== Video Notification Preferences ===")
        Log.d(TAG, "All notifications: ${areVideoNotificationsEnabled()}")
        Log.d(TAG, "Video favorited: ${areVideoFavoritedNotificationsEnabled()}")
        Log.d(TAG, "Video uploaded: ${areVideoUploadedNotificationsEnabled()}")
        Log.d(TAG, "======================================")
    }
}
