package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName


data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean = false,
    
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: Any? = null,
    
    @SerializedName("timestamp")
    val timestamp: String? = null,
    
    @SerializedName("statusCode")
    val statusCode: Int? = null,
    
    @SerializedName("errors")
    val errors: Any? = null, // Can be string (error code) or map
    
    @SerializedName("error_code")
    val errorCode: String? = null
)