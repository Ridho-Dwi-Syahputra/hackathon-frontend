package com.sako.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sako.R
import com.sako.MainActivity
import android.util.Log

/**
 * Firebase Cloud Messaging Service
 * Handles FCM notifications for SAKO app
 * Channels: sako_default, sako_map_reviews, sako_map_visits
 */
class FCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM_SERVICE"
        
        // Notification Channels sesuai backend
        const val CHANNEL_DEFAULT = "sako_default"
        const val CHANNEL_MAP_REVIEWS = "sako_map_reviews"
        const val CHANNEL_MAP_VISITS = "sako_map_visits"
        
        // Notification IDs
        const val NOTIFICATION_ID_DEFAULT = 1001
        const val NOTIFICATION_ID_MAP_REVIEW = 1002
        const val NOTIFICATION_ID_MAP_VISIT = 1003
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "📨 FCM Message received from: ${remoteMessage.from}")
        Log.d(TAG, "📋 Message data: ${remoteMessage.data}")
        
        // Handle notification data
        val notificationData = remoteMessage.data
        val notificationType = notificationData["type"] ?: "default"
        val module = notificationData["module"] ?: "general"
        
        Log.d(TAG, "🔔 Notification type: $notificationType, module: $module")
        
        // Handle different notification types
        when (notificationType) {
            "review_added" -> handleReviewAddedNotification(remoteMessage)
            "place_visited" -> handlePlaceVisitedNotification(remoteMessage)
            else -> handleDefaultNotification(remoteMessage)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "🆕 New FCM token: $token")
        
        // TODO: Send token to backend
        sendTokenToBackend(token)
    }

    private fun handleReviewAddedNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "Ulasan Berhasil Ditambahkan!"
        val body = remoteMessage.notification?.body ?: "Terima kasih atas ulasan Anda!"
        val data = remoteMessage.data
        
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigation_target", "PlaceDetailScreen")
            putExtra("place_id", data["tourist_place_id"])
            putExtra("place_name", data["place_name"])
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        showNotification(
            title = title,
            body = body,
            intent = intent,
            notificationId = NOTIFICATION_ID_MAP_REVIEW,
            channelId = CHANNEL_MAP_REVIEWS
        )
        
        Log.d(TAG, "⭐ Review notification shown for place: ${data["place_name"]}")
    }

    private fun handlePlaceVisitedNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "Tempat Wisata Dikunjungi!"
        val body = remoteMessage.notification?.body ?: "Jangan lupa tinggalkan ulasan!"
        val data = remoteMessage.data
        
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigation_target", "AddReviewScreen")
            putExtra("place_id", data["tourist_place_id"])
            putExtra("place_name", data["place_name"])
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        showNotification(
            title = title,
            body = body,
            intent = intent,
            notificationId = NOTIFICATION_ID_MAP_VISIT,
            channelId = CHANNEL_MAP_VISITS
        )
        
        Log.d(TAG, "🏛️ Visit notification shown for place: ${data["place_name"]}")
    }

    private fun handleDefaultNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "SAKO Notification"
        val body = remoteMessage.notification?.body ?: "Ada notifikasi baru untuk Anda"
        
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        showNotification(
            title = title,
            body = body,
            intent = intent,
            notificationId = NOTIFICATION_ID_DEFAULT,
            channelId = CHANNEL_DEFAULT
        )
        
        Log.d(TAG, "🔔 Default notification shown")
    }

    private fun showNotification(
        title: String,
        body: String,
        intent: Intent,
        notificationId: Int,
        channelId: String
    ) {
        val pendingIntent = PendingIntent.getActivity(
            this, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Add your notification icon
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary)) // Add your primary color
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Default Channel
            val defaultChannel = NotificationChannel(
                CHANNEL_DEFAULT,
                "SAKO Default",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General notifications from SAKO app"
            }

            // Map Reviews Channel
            val reviewsChannel = NotificationChannel(
                CHANNEL_MAP_REVIEWS,
                "Map Reviews",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications about review activities"
            }

            // Map Visits Channel
            val visitsChannel = NotificationChannel(
                CHANNEL_MAP_VISITS,
                "Map Visits",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about place visits"
            }

            notificationManager.createNotificationChannels(
                listOf(defaultChannel, reviewsChannel, visitsChannel)
            )

            Log.d(TAG, "📱 Notification channels created")
        }
    }

    private fun sendTokenToBackend(token: String) {
        // TODO: Send FCM token to backend when user login/register
        Log.d(TAG, "🔄 Should send token to backend: $token")
        
        // This will be handled by AuthRepository during login/register
        // Backend expects fcm_token field in login/register requests
    }
}