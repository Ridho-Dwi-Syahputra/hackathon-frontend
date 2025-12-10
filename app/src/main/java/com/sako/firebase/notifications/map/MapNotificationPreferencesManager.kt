package com.sako.firebase.notifications.map

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Map Notification Preferences Manager
 * Manages user preferences for map module notifications
 */
class MapNotificationPreferencesManager private constructor(
    private val context: Context
) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )
    
    companion object {
        private const val TAG = "MAP_NOTIFICATION_PREFS"
        private const val PREF_NAME = "map_notification_preferences"
        
        // Preference keys
        private const val KEY_REVIEW_NOTIFICATIONS = "review_notifications_enabled"
        private const val KEY_VISIT_NOTIFICATIONS = "visit_notifications_enabled"
        private const val KEY_ALL_NOTIFICATIONS = "map_notifications_enabled"
        
        @Volatile
        private var INSTANCE: MapNotificationPreferencesManager? = null
        
        fun getInstance(context: Context): MapNotificationPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = MapNotificationPreferencesManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Check if review notifications are enabled
     */
    fun areReviewNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_REVIEW_NOTIFICATIONS, true)
    }
    
    /**
     * Check if visit notifications are enabled  
     */
    fun areVisitNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VISIT_NOTIFICATIONS, true)
    }
    
    /**
     * Check if all map notifications are enabled
     */
    fun areMapNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_ALL_NOTIFICATIONS, true)
    }
    
    /**
     * Set review notifications enabled/disabled
     */
    fun setReviewNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_REVIEW_NOTIFICATIONS, enabled)
            .apply()
        Log.d(TAG, "Review notifications ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Set visit notifications enabled/disabled
     */
    fun setVisitNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_VISIT_NOTIFICATIONS, enabled)
            .apply()
        Log.d(TAG, "Visit notifications ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Set all map notifications enabled/disabled
     */
    fun setMapNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_ALL_NOTIFICATIONS, enabled)
            .apply()
        Log.d(TAG, "All map notifications ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Check if specific notification type should be shown
     */
    fun shouldShowNotification(notificationType: String): Boolean {
        // First check if all map notifications are enabled
        if (!areMapNotificationsEnabled()) {
            return false
        }
        
        // Then check specific notification type
        return when (notificationType) {
            "review_added" -> areReviewNotificationsEnabled()
            "place_visited" -> areVisitNotificationsEnabled()
            else -> true // Show unknown types by default if map notifications are enabled
        }
    }
    
    /**
     * Reset all preferences to default (all enabled)
     */
    fun resetToDefault() {
        sharedPreferences.edit()
            .putBoolean(KEY_REVIEW_NOTIFICATIONS, true)
            .putBoolean(KEY_VISIT_NOTIFICATIONS, true)
            .putBoolean(KEY_ALL_NOTIFICATIONS, true)
            .apply()
        Log.d(TAG, "Map notification preferences reset to default")
    }
    
    /**
     * Log current preferences for debugging
     */
    fun logCurrentPreferences() {
        Log.d(TAG, "=== Map Notification Preferences ===")
        Log.d(TAG, "All notifications: ${areMapNotificationsEnabled()}")
        Log.d(TAG, "Review notifications: ${areReviewNotificationsEnabled()}")
        Log.d(TAG, "Visit notifications: ${areVisitNotificationsEnabled()}")
        Log.d(TAG, "====================================")
    }
}