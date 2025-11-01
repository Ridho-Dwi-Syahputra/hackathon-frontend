package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Update Profile Request
data class UpdateProfileRequest(
    @SerializedName("full_name")
    val fullName: String
)