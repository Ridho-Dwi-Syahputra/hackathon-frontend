package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName


data class ErrorResponse(
    @SerializedName("status")
    val status: String = "error",

    @SerializedName("message")
    val message: String,

    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null,

    @SerializedName("error_code")
    val errorCode: String? = null
)