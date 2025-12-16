package com.sako.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sako.MainActivity
import com.sako.R
import com.sako.firebase.notifications.map.MapNotificationHandler
import com.sako.firebase.notifications.map.MapNotificationManager
import com.sako.firebase.notifications.quiz.QuizNotificationHandler
import com.sako.firebase.notifications.quiz.QuizNotificationManager
import com.sako.firebase.notifications.video.VideoNotificationHandler
import com.sako.firebase.notifications.video.VideoNotificationManager

/**
 * Firebase Cloud Messaging Service
 * Handles FCM token refresh and message receiving
 * 
 * @author SAKO Development Team
 */
class SakoFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "SAKO_FCM_SERVICE"
        private const val CHANNEL_ID = "sako_notifications"
        private const val CHANNEL_NAME = "Sako Notifications"
        private const val NOTIFICATION_ID = 1001
        
        // Quiz notification channels
        private const val QUIZ_SUCCESS_CHANNEL_ID = "sako_quiz_success"
        private const val QUIZ_GENERAL_CHANNEL_ID = "sako_quiz_general"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "✅ SakoFirebaseMessagingService created")
        createNotificationChannel()
    }

    /**
     * Called when a new FCM token is generated
     * This happens on app start and whenever token is refreshed
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "🔄 FCM Token refreshed: ${token.take(50)}...")
        
        // TODO: Send token to server
        // Kirim token baru ke backend saat token refresh
        sendTokenToServer(token)
    }

    /**
     * Called when a message is received while app is in foreground
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "📨 FCM Message received from: ${remoteMessage.from}")
        
        val notificationData = remoteMessage.data
        Log.d(TAG, "📊 Message data: $notificationData")
        
        // Check module type
        val module = notificationData["module"]
        
        // Handle different modules
        when (module) {
            "map" -> {
                handleMapNotification(notificationData, remoteMessage.notification)
                return
            }
            "video" -> {
                handleVideoNotification(notificationData, remoteMessage.notification)
                return
            }
            "quiz" -> {
                handleQuizNotification(notificationData, remoteMessage.notification)
                return
            }
        }

        // Handle regular notifications
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "📢 Regular notification - Title: ${notification.title}")
            showNotification(
                title = notification.title ?: "Sako App",
                body = notification.body ?: "You have a new notification",
                data = notificationData
            )
        }
        
        // Handle data-only messages for non-map/video/quiz modules
        if (notificationData.isNotEmpty() && module !in listOf("map", "video", "quiz")) {
            handleDataMessage(notificationData)
        }
    }
    
    /**
     * Handle video-specific notifications with preferences check
     */
    private fun handleVideoNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        try {
            val notificationType = data["type"] ?: return
            
            Log.d(TAG, "🎬 Video notification type: $notificationType")
            
            // Check user preferences first
            val notificationManager = VideoNotificationManager.getInstance(this)
            if (!notificationManager.shouldProcessNotification(notificationType)) {
                Log.d(TAG, "🔕 Video notification blocked by user preferences: $notificationType")
                return
            }
            
            // Process the notification through video handler
            val processed = VideoNotificationHandler.processVideoNotification(this, data)
            if (!processed) {
                Log.w(TAG, "⚠️ Failed to process video notification")
                return
            }
            
            // Create and show notification
            val (title, body) = if (notification != null) {
                Pair(
                    notification.title ?: "Sako Video",
                    notification.body ?: "Ada aktivitas di video"
                )
            } else {
                VideoNotificationHandler.createNotificationContent(notificationType, data)
            }
            
            // Add navigation data for video notifications
            val navigationData = VideoNotificationHandler.getNavigationData(notificationType, data)
            val enhancedData = data.toMutableMap().apply {
                putAll(navigationData)
            }
            
            showNotification(title, body, enhancedData)
            Log.d(TAG, "✅ Video notification shown: $title")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error handling video notification: ${e.message}")
        }
    }
    
    /**
     * Handle map-specific notifications with preferences check
     */
    private fun handleMapNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        try {
            val notificationType = data["type"] ?: return
            
            // Check user preferences first
            val notificationManager = MapNotificationManager.getInstance(this)
            if (!notificationManager.shouldProcessNotification(notificationType)) {
                Log.d(TAG, "🔕 Map notification blocked by user preferences: $notificationType")
                return
            }
            
            // Process the notification through map handler
            val processed = MapNotificationHandler.processMapNotification(this, data)
            if (!processed) {
                Log.w(TAG, "⚠️ Failed to process map notification")
                return
            }
            
            // Create and show notification
            val (title, body) = if (notification != null) {
                Pair(notification.title ?: "Sako Map", notification.body ?: "Ada aktivitas di peta")
            } else {
                MapNotificationHandler.createNotificationContent(notificationType, data)
            }
            
            // Add navigation data for map notifications
            val navigationData = MapNotificationHandler.getNavigationData(notificationType, data)
            val enhancedData = data.toMutableMap().apply {
                putAll(navigationData)
            }
            
            showNotification(title, body, enhancedData)
            Log.d(TAG, "✅ Map notification shown: $title")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error handling map notification: ${e.message}")
        }
    }

    /**
     * Handle quiz-specific notifications with preferences check
     */
    private fun handleQuizNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        try {
            val notificationType = data["type"] ?: return
            
            // Check user preferences first
            val notificationManager = QuizNotificationManager.getInstance(this)
            if (!notificationManager.shouldProcessNotification(notificationType)) {
                Log.d(TAG, "🔕 Quiz notification blocked by user preferences: $notificationType")
                return
            }
            
            // Process the notification through quiz handler
            val processed = QuizNotificationHandler.processQuizNotification(this, data)
            if (!processed) {
                Log.w(TAG, "⚠️ Failed to process quiz notification")
                return
            }
            
            // Create and show notification
            val (title, body) = if (notification != null) {
                Pair(notification.title ?: "Sako Quiz", notification.body ?: "Ada aktivitas di kuis")
            } else {
                QuizNotificationHandler.createNotificationContent(notificationType, data)
            }
            
            // Add navigation data for quiz notifications
            val navigationData = QuizNotificationHandler.getNavigationData(notificationType, data)
            val enhancedData = data.toMutableMap().apply {
                putAll(navigationData)
            }
            
            showNotification(title, body, enhancedData)
            Log.d(TAG, "✅ Quiz notification shown: $title")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error handling quiz notification: ${e.message}")
        }
    }

    /**
     * Create notification channel for Android 8.0+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            // Main channel
            val mainChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications from Sako Cultural App"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager?.createNotificationChannel(mainChannel)
            
            // Quiz success channel (for passed and perfect score)
            val quizSuccessChannel = NotificationChannel(
                QUIZ_SUCCESS_CHANNEL_ID,
                "Quiz Success Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when you pass a quiz"
                enableLights(true)
                enableVibration(true)
                lightColor = android.graphics.Color.GREEN
            }
            notificationManager?.createNotificationChannel(quizSuccessChannel)
            
            // Quiz general channel (for failed quizzes)
            val quizGeneralChannel = NotificationChannel(
                QUIZ_GENERAL_CHANNEL_ID,
                "Quiz Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General quiz notifications"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager?.createNotificationChannel(quizGeneralChannel)
            
            Log.d(TAG, "📱 Notification channels created: main, quiz_success, quiz_general")
        }
    }

    /**
     * Show notification to user
     */
    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Add data from FCM message as extras
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Determine channel and priority based on notification type
        val channelId = when (data["module"]) {
            "quiz" -> {
                when (data["type"]) {
                    "quiz_perfect_score", "quiz_passed" -> QUIZ_SUCCESS_CHANNEL_ID
                    "quiz_failed", "quiz_completed" -> QUIZ_GENERAL_CHANNEL_ID
                    else -> CHANNEL_ID
                }
            }
            else -> CHANNEL_ID
        }
        
        val priority = when (data["module"]) {
            "quiz" -> NotificationCompat.PRIORITY_HIGH
            else -> NotificationCompat.PRIORITY_DEFAULT
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(priority)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        val notificationManager = getSystemService(NotificationManager::class.java)
        // Use unique notification ID based on module to allow multiple notifications
        val notificationId = when (data["module"]) {
            "quiz" -> NOTIFICATION_ID + 1
            "map" -> NOTIFICATION_ID + 2
            "video" -> NOTIFICATION_ID + 3
            else -> NOTIFICATION_ID
        }
        notificationManager?.notify(notificationId, notificationBuilder.build())
        
        Log.d(TAG, "🔔 Notification shown on channel $channelId: $title - $body")
    }

    /**
     * Handle data-only messages (background processing)
     */
    private fun handleDataMessage(data: Map<String, String>) {
        Log.d(TAG, "🔄 Handling data message: $data")
        
        // Handle different types of data messages
        when (data["type"]) {
            "map_notification" -> {
                Log.d(TAG, "🗺️ Map notification received")
                // Handle map-related notifications
            }
            "review_notification" -> {
                Log.d(TAG, "⭐ Review notification received") 
                // Handle review-related notifications
            }
            "system_announcement" -> {
                Log.d(TAG, "📢 System announcement received")
                // Handle system announcements
            }
            else -> {
                Log.d(TAG, "❓ Unknown message type: ${data["type"]}")
            }
        }
    }

    /**
     * Send token to backend server
     */
    private fun sendTokenToServer(token: String) {
        Log.d(TAG, "📤 Sending token to server: ${token.take(50)}...")
        
        // TODO: Implement API call to update FCM token in backend
        // This should be called whenever token is refreshed
        /*
        try {
            // Example implementation:
            val apiService = ApiConfig.getApiService(this)
            val updateTokenRequest = UpdateFCMTokenRequest(token)
            
            // Make async call to update token
            lifecycleScope.launch {
                try {
                    apiService.updateFCMToken(updateTokenRequest)
                    Log.d(TAG, "✅ Token berhasil dikirim ke server")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Gagal kirim token ke server: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error sending token: ${e.message}")
        }
        */
    }
}