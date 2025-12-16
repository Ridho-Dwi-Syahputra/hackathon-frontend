package com.sako.firebase.notifications.quiz

import android.content.Context
import android.util.Log

/**
 * Quiz Notification Handler
 * Handles FCM notifications specifically for quiz module events
 */
object QuizNotificationHandler {
    
    private const val TAG = "QUIZ_NOTIFICATION_HANDLER"

    /**
     * Process FCM notification data for quiz module
     * Called by SakoFirebaseMessagingService when module="quiz"
     */
    fun processQuizNotification(
        context: Context,
        notificationData: Map<String, String>
    ): Boolean {
        try {
            val notificationType = notificationData["type"] ?: return false
            val levelName = notificationData["level_name"] ?: "Unknown Level"
            val userName = notificationData["user_name"] ?: "Unknown User"
            
            Log.d(TAG, "🎯 Processing quiz notification: type=$notificationType, level=$levelName")
            
            when (notificationType) {
                "quiz_perfect_score" -> {
                    handlePerfectScoreNotification(context, notificationData)
                }
                "quiz_passed" -> {
                    handleQuizPassedNotification(context, notificationData)
                }
                "quiz_failed" -> {
                    handleQuizFailedNotification(context, notificationData)
                }
                "quiz_completed" -> {
                    handleQuizCompletedNotification(context, notificationData)
                }
                else -> {
                    Log.w(TAG, "⚠️ Unknown quiz notification type: $notificationType")
                    return false
                }
            }
            
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error processing quiz notification: ${e.message}")
            return false
        }
    }

    /**
     * Handle perfect score notification
     */
    private fun handlePerfectScoreNotification(
        context: Context,
        data: Map<String, String>
    ) {
        val levelName = data["level_name"] ?: "Unknown Level"
        val userName = data["user_name"] ?: "Unknown User"
        val xpEarned = data["xp_earned"] ?: "0"
        val attemptId = data["attempt_id"] ?: ""
        
        Log.d(TAG, "🏆 Perfect score notification: $userName got 100% on $levelName (+$xpEarned XP)")
        
        // Log untuk debugging backend integration
        Log.d(TAG, "Perfect score details - Attempt ID: $attemptId, XP: $xpEarned")
    }

    /**
     * Handle quiz passed notification
     */
    private fun handleQuizPassedNotification(
        context: Context,
        data: Map<String, String>
    ) {
        val levelName = data["level_name"] ?: "Unknown Level"
        val userName = data["user_name"] ?: "Unknown User"
        val percentCorrect = data["percent_correct"] ?: "0"
        val xpEarned = data["xp_earned"] ?: "0"
        val attemptId = data["attempt_id"] ?: ""
        
        Log.d(TAG, "✅ Quiz passed notification: $userName passed $levelName ($percentCorrect%, +$xpEarned XP)")
        
        // Log untuk debugging backend integration
        Log.d(TAG, "Quiz passed details - Attempt ID: $attemptId, Score: $percentCorrect%")
    }

    /**
     * Handle quiz failed notification
     */
    private fun handleQuizFailedNotification(
        context: Context,
        data: Map<String, String>
    ) {
        val levelName = data["level_name"] ?: "Unknown Level"
        val userName = data["user_name"] ?: "Unknown User"
        val percentCorrect = data["percent_correct"] ?: "0"
        val attemptId = data["attempt_id"] ?: ""
        
        Log.d(TAG, "😢 Quiz failed notification: $userName didn't pass $levelName ($percentCorrect%)")
        
        // Log untuk debugging backend integration
        Log.d(TAG, "Quiz failed details - Attempt ID: $attemptId, Score: $percentCorrect%")
    }

    /**
     * Handle general quiz completed notification
     */
    private fun handleQuizCompletedNotification(
        context: Context,
        data: Map<String, String>
    ) {
        val levelName = data["level_name"] ?: "Unknown Level"
        val userName = data["user_name"] ?: "Unknown User"
        val scorePoints = data["score_points"] ?: "0"
        val attemptId = data["attempt_id"] ?: ""
        
        Log.d(TAG, "🎯 Quiz completed notification: $userName completed $levelName (Score: $scorePoints)")
        
        // Log untuk debugging backend integration
        Log.d(TAG, "Quiz completed details - Attempt ID: $attemptId")
    }

    /**
     * Create notification title and body for quiz events
     */
    fun createNotificationContent(
        notificationType: String,
        data: Map<String, String>
    ): Pair<String, String> {
        val levelName = data["level_name"] ?: "Kuis"
        val userName = data["user_name"] ?: "Pengguna"
        val percentCorrect = data["percent_correct"]?.toDoubleOrNull()?.toInt() ?: 0
        val xpEarned = data["xp_earned"] ?: "0"
        
        return when (notificationType) {
            "quiz_perfect_score" -> {
                Pair(
                    "🏆 PERFECT SCORE!",
                    "Luar biasa, $userName! Kamu mendapat nilai sempurna di kuis \"$levelName\"! Kamu mendapat $xpEarned XP! 🎉"
                )
            }
            "quiz_passed" -> {
                Pair(
                    "✅ Selamat, Kamu Lulus!",
                    "Hebat, $userName! Kamu berhasil menyelesaikan kuis \"$levelName\" dengan skor $percentCorrect%. Kamu mendapat $xpEarned XP! 🎯"
                )
            }
            "quiz_failed" -> {
                Pair(
                    "😢 Belum Berhasil",
                    "Tetap semangat, $userName! Kuis \"$levelName\" belum berhasil diselesaikan (skor $percentCorrect%). Coba lagi, kamu pasti bisa! 💪"
                )
            }
            "quiz_completed" -> {
                Pair(
                    "🎯 Kuis Selesai",
                    "$userName telah menyelesaikan kuis \"$levelName\""
                )
            }
            else -> {
                Pair("Notifikasi Kuis", "Ada aktivitas baru di kuis")
            }
        }
    }

    /**
     * Get navigation intent data for quiz notifications
     */
    fun getNavigationData(
        notificationType: String,
        data: Map<String, String>
    ): Map<String, String> {
        val attemptId = data["attempt_id"] ?: ""
        
        return when (notificationType) {
            "quiz_perfect_score", "quiz_passed", "quiz_failed", "quiz_completed" -> mapOf(
                "screen" to "quiz_result",
                "attempt_id" to attemptId,
                "show_celebration" to (notificationType == "quiz_perfect_score").toString()
            )
            else -> mapOf("screen" to "quiz_home")
        }
    }
}
