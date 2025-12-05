package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Scan QR Code Request
data class ScanQRRequest(
    @SerializedName("qr_code_value")
    val qrCodeValue: String
)

// Checkin Location Request - Scan QR Code
data class CheckinLocationRequest(
    @SerializedName("qr_code_value")
    val qrCodeValue: String
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

// Update Review Request
data class UpdateReviewRequest(
    @SerializedName("tourist_place_id")
    val touristPlaceId: String,
    
    @SerializedName("rating")
    val rating: Int,

    @SerializedName("review_text")
    val reviewText: String?
)