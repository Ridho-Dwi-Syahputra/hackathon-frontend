package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ProfileData?
)

data class ProfileData(
    @SerializedName("user")
    val user: ProfileUserData,

    @SerializedName("stats")
    val stats: UserStats,

    @SerializedName("badges")
    val badges: List<BadgeItem>
)

data class UserStats(
    @SerializedName("totalPoints")
    val totalPoints: Int,

    @SerializedName("totalAttempts")
    val totalAttempts: Int,

    @SerializedName("completedLevels")
    val completedLevels: Int,

    @SerializedName("visitedPlaces")
    val visitedPlaces: Int
)

data class BadgeItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("imageUrl")
    val imageUrl: String?,

    @SerializedName("earnedAt")
    val earnedAt: String
)

// Profile user data untuk menghindari konflik dengan AuthResponse.UserData
data class ProfileUserData(
    @SerializedName("id")
    val id: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("totalXp")
    val totalXp: Int = 0,

    @SerializedName("status")
    val status: String,

    @SerializedName("userImageUrl")
    val userImageUrl: String?,

    @SerializedName("fcmToken")
    val fcmToken: String? = null,

    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    
    @SerializedName("notification_preferences")
    val notificationPreferences: String? = null
)

// Notification Preferences Response
data class NotificationPreferencesResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: NotificationPreferencesData? = null
)

data class NotificationPreferencesData(
    @SerializedName("notification_preferences")
    val notificationPreferences: com.sako.data.remote.request.NotificationPreferences
)

// ============================================================================
// BADGE SYSTEM RESPONSES
// ============================================================================

// Response untuk GET /api/badges (All badges dengan status)
data class AllBadgesResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: BadgeListData
)

data class BadgeListData(
    @SerializedName("owned")
    val owned: List<Badge>,
    
    @SerializedName("locked")
    val locked: List<Badge>,
    
    @SerializedName("progress")
    val progress: Map<String, BadgeProgress>? = null,
    
    @SerializedName("totalBadges")
    val totalBadges: Int,
    
    @SerializedName("earnedCount")
    val earnedCount: Int
)

data class Badge(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("image_url")
    val imageUrl: String?,
    
    @SerializedName("criteria_type")
    val criteriaType: String,
    
    @SerializedName("is_earned")
    val isEarned: Int? = null,
    
    @SerializedName("earned_at")
    val earnedAt: String? = null,
    
    @SerializedName("is_viewed")
    val isViewed: Int? = null
)

data class BadgeProgress(
    @SerializedName("current")
    val current: Int,
    
    @SerializedName("target")
    val target: Int,
    
    @SerializedName("percentage")
    val percentage: Float
)

// Response untuk GET /api/badges/user (User's badges)
data class UserBadgesResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: List<Badge>
)

// Response untuk GET /api/badges/unviewed
data class UnviewedBadgesResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: List<Badge>
)

// Response untuk POST /api/badges/:id/view
data class BadgeViewResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String
)