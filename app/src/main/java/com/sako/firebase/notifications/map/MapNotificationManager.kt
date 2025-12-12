package com.sako.firebase.notifications.map

import android.content.Context
import android.util.Log
import com.sako.firebase.FirebaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Map Notification Manager
 * Coordinates map notification subscriptions and preferences
 */
class MapNotificationManager private constructor(
    private val context: Context
) {
    
    private val preferencesManager = MapNotificationPreferencesManager.getInstance(context)
    
    companion object {
        private const val TAG = "MAP_NOTIFICATION_MANAGER"
        
        // FCM Topics for map notifications
        private const val TOPIC_MAP_ALL = "map_notifications"
        private const val TOPIC_MAP_REVIEWS = "map_review_notifications" 
        private const val TOPIC_MAP_VISITS = "map_visit_notifications"
        
        @Volatile
        private var INSTANCE: MapNotificationManager? = null
        
        fun getInstance(context: Context): MapNotificationManager {
            return INSTANCE ?: synchronized(this) {
                val instance = MapNotificationManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Initialize map notifications on app start or user login
     */
    fun initializeMapNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🚀 Initializing map notifications")
                
                // Log current preferences
                preferencesManager.logCurrentPreferences()
                
                // Subscribe to topics based on preferences
                if (preferencesManager.areMapNotificationsEnabled()) {
                    subscribeToMapTopics()
                } else {
                    Log.d(TAG, "🔕 Map notifications disabled, skipping subscription")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error initializing map notifications: ${e.message}")
            }
        }
    }
    
    /**
     * Subscribe to FCM topics for map notifications
     */
    private suspend fun subscribeToMapTopics() {
        try {
            // Subscribe to general map topic
            FirebaseHelper.subscribeToTopic(TOPIC_MAP_ALL)
            
            // Subscribe to specific topics based on preferences
            if (preferencesManager.areReviewNotificationsEnabled()) {
                FirebaseHelper.subscribeToTopic(TOPIC_MAP_REVIEWS)
                Log.d(TAG, "✅ Subscribed to review notifications")
            }
            
            if (preferencesManager.areVisitNotificationsEnabled()) {
                FirebaseHelper.subscribeToTopic(TOPIC_MAP_VISITS)
                Log.d(TAG, "✅ Subscribed to visit notifications")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error subscribing to map topics: ${e.message}")
        }
    }
    
    /**
     * Unsubscribe from FCM topics for map notifications
     */
    private suspend fun unsubscribeFromMapTopics() {
        try {
            FirebaseHelper.unsubscribeFromTopic(TOPIC_MAP_ALL)
            FirebaseHelper.unsubscribeFromTopic(TOPIC_MAP_REVIEWS)
            FirebaseHelper.unsubscribeFromTopic(TOPIC_MAP_VISITS)
            Log.d(TAG, "✅ Unsubscribed from all map topics")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error unsubscribing from map topics: ${e.message}")
        }
    }
    
    /**
     * Enable/disable review notifications
     */
    fun setReviewNotificationsEnabled(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            preferencesManager.setReviewNotificationsEnabled(enabled)
            
            if (preferencesManager.areMapNotificationsEnabled()) {
                if (enabled) {
                    FirebaseHelper.subscribeToTopic(TOPIC_MAP_REVIEWS)
                } else {
                    FirebaseHelper.unsubscribeFromTopic(TOPIC_MAP_REVIEWS)
                }
            }
        }
    }
    
    /**
     * Enable/disable visit notifications
     */
    fun setVisitNotificationsEnabled(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            preferencesManager.setVisitNotificationsEnabled(enabled)
            
            if (preferencesManager.areMapNotificationsEnabled()) {
                if (enabled) {
                    FirebaseHelper.subscribeToTopic(TOPIC_MAP_VISITS)
                } else {
                    FirebaseHelper.unsubscribeFromTopic(TOPIC_MAP_VISITS)
                }
            }
        }
    }
    
    /**
     * Enable/disable all map notifications
     */
    fun setMapNotificationsEnabled(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            preferencesManager.setMapNotificationsEnabled(enabled)
            
            if (enabled) {
                subscribeToMapTopics()
            } else {
                unsubscribeFromMapTopics()
            }
        }
    }
    
    /**
     * Check if notification should be processed based on user preferences
     */
    fun shouldProcessNotification(notificationType: String): Boolean {
        return preferencesManager.shouldShowNotification(notificationType)
    }
    
    /**
     * Get current notification preferences for UI
     */
    fun getCurrentPreferences(): MapNotificationPreferences {
        return MapNotificationPreferences(
            allEnabled = preferencesManager.areMapNotificationsEnabled(),
            reviewEnabled = preferencesManager.areReviewNotificationsEnabled(),
            visitEnabled = preferencesManager.areVisitNotificationsEnabled()
        )
    }
}

/**
 * Data class for map notification preferences
 */
data class MapNotificationPreferences(
    val allEnabled: Boolean,
    val reviewEnabled: Boolean,
    val visitEnabled: Boolean
)