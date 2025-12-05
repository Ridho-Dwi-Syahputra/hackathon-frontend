package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Login Request
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,
    
    @SerializedName("fcm_token")
    val fcmToken: String? = null
)

// Register Request
data class RegisterRequest(
    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,
    
    @SerializedName("fcm_token")
    val fcmToken: String? = null
)