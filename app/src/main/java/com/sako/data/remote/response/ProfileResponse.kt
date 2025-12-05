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
    @SerializedName("total_points")
    val totalPoints: Int,

    @SerializedName("total_attempts")
    val totalAttempts: Int,

    @SerializedName("completed_levels")
    val completedLevels: Int,

    @SerializedName("visited_places")
    val visitedPlaces: Int
)

data class BadgeItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("earned_at")
    val earnedAt: String
)

// Profile user data untuk menghindari konflik dengan AuthResponse.UserData
data class ProfileUserData(
    @SerializedName("users_id")
    val id: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("total_xp")
    val totalXp: Int = 0,

    @SerializedName("status")
    val status: String,

    @SerializedName("user_image_url")
    val userImageUrl: String?,

    @SerializedName("fcm_token")
    val fcmToken: String? = null,

    @SerializedName("created_at")
    val createdAt: String
)