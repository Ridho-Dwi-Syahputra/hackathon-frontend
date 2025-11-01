package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Login Request
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

// Register Request
data class RegisterRequest(
    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

// Change Password Request
data class ChangePasswordRequest(
    @SerializedName("old_password")
    val oldPassword: String,

    @SerializedName("new_password")
    val newPassword: String
)