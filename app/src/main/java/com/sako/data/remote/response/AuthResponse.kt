package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AuthData?
)

data class AuthData(
    @SerializedName("user")
    val user: UserData,

    @SerializedName("token")
    val token: String
)

data class UserData(
    @SerializedName("id")
    val id: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("total_xp")
    val totalXp: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("user_image_url")
    val userImageUrl: String?,

    @SerializedName("created_at")
    val createdAt: String
)