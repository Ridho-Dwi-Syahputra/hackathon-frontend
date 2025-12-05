package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Update Profile Request
data class UpdateProfileRequest(
    @SerializedName("full_name")
    val fullName: String
)

// Change Password Request
data class ChangePasswordRequest(
    @SerializedName("old_password")
    val oldPassword: String,
    
    @SerializedName("new_password") 
    val newPassword: String
)