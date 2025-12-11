package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Update Profile Request
data class UpdateProfileRequest(
    @SerializedName("full_name")
    val fullName: String,
    
    @SerializedName("email")
    val email: String
)

// Change Password Request
data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    
    @SerializedName("new_password") 
    val newPassword: String
)

// Update Notification Preferences Request
data class UpdateNotificationPreferencesRequest(
    @SerializedName("notification_preferences")
    val notificationPreferences: NotificationPreferences
)

// Notification Preferences Structure
data class NotificationPreferences(
    @SerializedName("system_announcements")
    val systemAnnouncements: Boolean = true,
    
    @SerializedName("marketing")
    val marketing: Boolean = false,
    
    @SerializedName("map_notifications")
    val mapNotifications: MapNotifications = MapNotifications(),
    
    @SerializedName("video_notifications")
    val videoNotifications: Boolean = true,
    
    @SerializedName("quiz_notifications")
    val quizNotifications: Boolean = true
)

data class MapNotifications(
    @SerializedName("review_added")
    val reviewAdded: Boolean = true,
    
    @SerializedName("place_visited")
    val placeVisited: Boolean = true
)