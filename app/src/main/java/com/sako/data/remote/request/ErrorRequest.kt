package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Error Log Request (jika backend support logging error dari client)
data class ErrorLogRequest(
    @SerializedName("error_message")
    val errorMessage: String,

    @SerializedName("error_type")
    val errorType: String,

    @SerializedName("screen_name")
    val screenName: String? = null,

    @SerializedName("timestamp")
    val timestamp: String
)