package com.sako.firebase.notifications.quiz

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Quiz Notification Preferences Manager
 * Manages local storage of quiz notification preferences
 */
class QuizNotificationPreferencesManager private constructor(
    context: Context
) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, 
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val TAG = "QUIZ_NOTIF_PREFS"
        private const val PREFS_NAME = "sako_quiz_notification_preferences"
        
        // Preference keys
        private const val KEY_QUIZ_NOTIFICATIONS_ENABLED = "quiz_notifications_enabled"
        private const val KEY_QUIZ_COMPLETED_ENABLED = "quiz_completed_enabled"
        private const val KEY_QUIZ_PERFECT_SCORE_ENABLED = "quiz_perfect_score_enabled"
        
        @Volatile
        private var INSTANCE: QuizNotificationPreferencesManager? = null
        
        fun getInstance(context: Context): QuizNotificationPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = QuizNotificationPreferencesManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Check if quiz notifications are enabled globally
     */
    fun areQuizNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_QUIZ_NOTIFICATIONS_ENABLED, true)
    }
    
    /**
     * Set global quiz notifications enabled/disabled
     */
    fun setQuizNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_QUIZ_NOTIFICATIONS_ENABLED, enabled)
            .apply()
        
        Log.d(TAG, "⚙️ Quiz notifications ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Check if quiz completed notifications are enabled
     */
    fun areCompletedNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_QUIZ_COMPLETED_ENABLED, true)
    }
    
    /**
     * Set quiz completed notifications enabled/disabled
     */
    fun setCompletedNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_QUIZ_COMPLETED_ENABLED, enabled)
            .apply()
        
        Log.d(TAG, "⚙️ Quiz completed notifications ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Check if perfect score notifications are enabled
     */
    fun arePerfectScoreNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_QUIZ_PERFECT_SCORE_ENABLED, true)
    }
    
    /**
     * Set perfect score notifications enabled/disabled
     */
    fun setPerfectScoreNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_QUIZ_PERFECT_SCORE_ENABLED, enabled)
            .apply()
        
        Log.d(TAG, "⚙️ Perfect score notifications ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Reset all preferences to default (all enabled)
     */
    fun resetToDefaults() {
        sharedPreferences.edit()
            .putBoolean(KEY_QUIZ_NOTIFICATIONS_ENABLED, true)
            .putBoolean(KEY_QUIZ_COMPLETED_ENABLED, true)
            .putBoolean(KEY_QUIZ_PERFECT_SCORE_ENABLED, true)
            .apply()
        
        Log.d(TAG, "🔄 Quiz notification preferences reset to defaults")
    }
    
    /**
     * Log current preferences for debugging
     */
    fun logCurrentPreferences() {
        Log.d(TAG, "📊 Current Quiz Notification Preferences:")
        Log.d(TAG, "  - Quiz Notifications: ${areQuizNotificationsEnabled()}")
        Log.d(TAG, "  - Quiz Completed: ${areCompletedNotificationsEnabled()}")
        Log.d(TAG, "  - Perfect Score: ${arePerfectScoreNotificationsEnabled()}")
    }
}
