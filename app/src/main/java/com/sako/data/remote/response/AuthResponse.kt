package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AuthData?
)

data class AuthData(
    @SerializedName("user")
    val user: UserData,
    
    @SerializedName("access_token") // Token JWT dari backend (1 jam)
    val accessToken: String? = null,
    
    @SerializedName("database_token") // Token database untuk auto-login (30 hari)
    val databaseToken: String? = null,
    
    @SerializedName("expires_in")
    val expiresIn: Int? = null
)

data class UserData(
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

// Update Profile Response
data class UpdateProfileResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UpdateProfileData?
)

data class ChangePasswordResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)

data class UpdateProfileData(
    @SerializedName("user")
    val user: UserData,
    
    @SerializedName("image_url")
    val imageUrl: String? = null
)