package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Checkin Location Request
data class CheckinLocationRequest(
    @SerializedName("qr_token")
    val qrToken: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)

// Add Review Request
data class AddReviewRequest(
    @SerializedName("tourist_place_id")
    val touristPlaceId: String,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("review_text")
    val reviewText: String?
)

// Update Review Request (sama seperti Add, tapi untuk update)
data class UpdateReviewRequest(
    @SerializedName("tourist_place_id")
    val touristPlaceId: String,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("review_text")
    val reviewText: String?
)