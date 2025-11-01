package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

data class TouristPlaceListResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<TouristPlaceItem>
)

data class TouristPlaceItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
    @SerializedName("location") val location: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

data class TouristPlaceDetailResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: TouristPlaceDetail
)

data class TouristPlaceDetail(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
    @SerializedName("location") val location: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("reviews") val reviews: List<Review>,
    @SerializedName("visited") val visited: Boolean
)

data class Review(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("username") val username: String,
    @SerializedName("userImage") val userImage: String?,
    @SerializedName("rating") val rating: Int,
    @SerializedName("content") val content: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("isOwner") val isOwner: Boolean
)

data class CheckinLocationResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: CheckinData
)

data class CheckinData(
    @SerializedName("locationId") val locationId: String,
    @SerializedName("visited") val visited: Boolean,
    @SerializedName("timestamp") val timestamp: String
)

data class AddReviewResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Review
)