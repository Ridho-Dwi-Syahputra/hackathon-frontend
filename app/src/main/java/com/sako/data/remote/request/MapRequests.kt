package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

data class CheckinLocationRequest(
    @SerializedName("locationId") val locationId: String,
    @SerializedName("token") val token: String
)

data class AddReviewRequest(
    @SerializedName("locationId") val locationId: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("content") val content: String
)

data class UpdateReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("content") val content: String
)