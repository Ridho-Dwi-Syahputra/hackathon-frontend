package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ProfileData?
)

data class ProfileData(
    @SerializedName("user")
    val user: UserData,

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

data class UpdateProfileResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UpdateProfileData?
)

data class UpdateProfileData(
    @SerializedName("user")
    val user: UserData,

    @SerializedName("image_url")
    val imageUrl: String?
)