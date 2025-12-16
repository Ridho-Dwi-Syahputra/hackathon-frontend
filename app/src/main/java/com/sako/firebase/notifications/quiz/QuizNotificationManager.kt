package com.sako.firebase.notifications.quiz

import android.content.Context
import android.util.Log
import com.sako.firebase.FirebaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Quiz Notification Manager
 * Coordinates quiz notification subscriptions and preferences
 */
class QuizNotificationManager private constructor(
    private val context: Context
) {
    
    private val preferencesManager = QuizNotificationPreferencesManager.getInstance(context)
    
    companion object {
        private const val TAG = "QUIZ_NOTIFICATION_MANAGER"
        
        // FCM Topics for quiz notifications
        private const val TOPIC_QUIZ_ALL = "quiz_notifications"
        private const val TOPIC_QUIZ_COMPLETED = "quiz_completed_notifications"
        private const val TOPIC_QUIZ_PERFECT = "quiz_perfect_score_notifications"
        
        @Volatile
        private var INSTANCE: QuizNotificationManager? = null
        
        fun getInstance(context: Context): QuizNotificationManager {
            return INSTANCE ?: synchronized(this) {
                val instance = QuizNotificationManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Initialize quiz notifications on app start or user login
     */
    fun initializeQuizNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🚀 Initializing quiz notifications")
                
                // Log current preferences
                preferencesManager.logCurrentPreferences()
                
                // Subscribe to topics based on preferences
                if (preferencesManager.areQuizNotificationsEnabled()) {
                    subscribeToQuizTopics()
                } else {
                    Log.d(TAG, "🔕 Quiz notifications disabled, skipping subscription")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error initializing quiz notifications: ${e.message}")
            }
        }
    }
    
    /**
     * Subscribe to FCM topics for quiz notifications
     */
    private suspend fun subscribeToQuizTopics() {
        try {
            // Subscribe to general quiz topic
            FirebaseHelper.subscribeToTopic(TOPIC_QUIZ_ALL)
            
            // Subscribe to specific topics based on preferences
            if (preferencesManager.areCompletedNotificationsEnabled()) {
                FirebaseHelper.subscribeToTopic(TOPIC_QUIZ_COMPLETED)
                Log.d(TAG, "✅ Subscribed to quiz completed notifications")
            }
            
            if (preferencesManager.arePerfectScoreNotificationsEnabled()) {
                FirebaseHelper.subscribeToTopic(TOPIC_QUIZ_PERFECT)
                Log.d(TAG, "✅ Subscribed to perfect score notifications")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error subscribing to quiz topics: ${e.message}")
        }
    }
    
    /**
     * Unsubscribe from FCM topics for quiz notifications
     */
    private suspend fun unsubscribeFromQuizTopics() {
        try {
            FirebaseHelper.unsubscribeFromTopic(TOPIC_QUIZ_ALL)
            FirebaseHelper.unsubscribeFromTopic(TOPIC_QUIZ_COMPLETED)
            FirebaseHelper.unsubscribeFromTopic(TOPIC_QUIZ_PERFECT)
            Log.d(TAG, "✅ Unsubscribed from all quiz topics")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error unsubscribing from quiz topics: ${e.message}")
        }
    }
    
    /**
     * Update notification preferences and adjust topic subscriptions
     */
    fun updateNotificationPreferences(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "⚙️ Updating quiz notification preferences: enabled=$enabled")
                
                preferencesManager.setQuizNotificationsEnabled(enabled)
                
                if (enabled) {
                    subscribeToQuizTopics()
                } else {
                    unsubscribeFromQuizTopics()
                }
                
                Log.d(TAG, "✅ Quiz notification preferences updated successfully")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error updating quiz notification preferences: ${e.message}")
            }
        }
    }
    
    /**
     * Update specific notification type preference
     */
    fun updateNotificationType(type: String, enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "⚙️ Updating quiz notification type: $type = $enabled")
                
                when (type) {
                    "quiz_completed" -> {
                        preferencesManager.setCompletedNotificationsEnabled(enabled)
                        if (enabled) {
                            FirebaseHelper.subscribeToTopic(TOPIC_QUIZ_COMPLETED)
                        } else {
                            FirebaseHelper.unsubscribeFromTopic(TOPIC_QUIZ_COMPLETED)
                        }
                    }
                    "quiz_perfect_score" -> {
                        preferencesManager.setPerfectScoreNotificationsEnabled(enabled)
                        if (enabled) {
                            FirebaseHelper.subscribeToTopic(TOPIC_QUIZ_PERFECT)
                        } else {
                            FirebaseHelper.unsubscribeFromTopic(TOPIC_QUIZ_PERFECT)
                        }
                    }
                }
                
                Log.d(TAG, "✅ Quiz notification type updated successfully")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error updating quiz notification type: ${e.message}")
            }
        }
    }
    
    /**
     * Check if a specific notification type should be processed
     */
    fun shouldProcessNotification(notificationType: String): Boolean {
        if (!preferencesManager.areQuizNotificationsEnabled()) {
            Log.d(TAG, "🔕 Quiz notifications disabled globally")
            return false
        }
        
        return when (notificationType) {
            "quiz_perfect_score" -> {
                preferencesManager.arePerfectScoreNotificationsEnabled()
            }
            "quiz_passed", "quiz_failed", "quiz_completed" -> {
                preferencesManager.areCompletedNotificationsEnabled()
            }
            else -> true
        }
    }
    
    /**
     * Get current preferences status
     */
    fun getPreferencesStatus(): Map<String, Boolean> {
        return mapOf(
            "quiz_notifications_enabled" to preferencesManager.areQuizNotificationsEnabled(),
            "quiz_completed_enabled" to preferencesManager.areCompletedNotificationsEnabled(),
            "quiz_perfect_score_enabled" to preferencesManager.arePerfectScoreNotificationsEnabled()
        )
    }
}
