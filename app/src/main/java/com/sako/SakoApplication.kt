package com.sako

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.sako.firebase.FirebaseHelper
import com.sako.firebase.notifications.map.MapNotificationManager
import com.sako.firebase.notifications.quiz.QuizNotificationManager
import com.sako.firebase.notifications.video.VideoNotificationManager

class SakoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize application-wide configurations
        Log.d("SakoApplication", "Application started")

        // Initialize Firebase
        FirebaseHelper.initialize(this)
        
        // Create notification channel for FCM
        createNotificationChannel()
        
        // Initialize Module Notification Managers
        Log.d("SakoApplication", "🔔 Initializing notification managers...")
        
        // Initialize Map Notifications
        val mapNotificationManager = MapNotificationManager.getInstance(this)
        mapNotificationManager.initializeMapNotifications()
        
        // Initialize Quiz Notifications
        val quizNotificationManager = QuizNotificationManager.getInstance(this)
        quizNotificationManager.initializeQuizNotifications()
        
        // Initialize Video Notifications
        val videoNotificationManager = VideoNotificationManager.getInstance(this)
        videoNotificationManager.initializeVideoNotifications()
        
        Log.d("SakoApplication", "✅ Firebase, notifications (Map, Quiz, Video) initialized")

        // TODO: Initialize Timber for logging (optional)
        // if (BuildConfig.DEBUG) {
        //     Timber.plant(Timber.DebugTree())
        // }

        // TODO: Initialize Crashlytics (optional)
        // FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "sako_notifications"
            val channelName = "Sako Cultural App Notifications"
            val channelDescription = "Notifications for Sako Cultural App"
            val importance = NotificationManager.IMPORTANCE_HIGH
            
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
            
            Log.d("SakoApplication", "Notification channel created: $channelId")
        }
    }
}