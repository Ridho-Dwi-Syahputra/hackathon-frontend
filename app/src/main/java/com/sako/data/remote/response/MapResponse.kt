package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

data class TouristPlaceListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<TouristPlaceItem>
)

data class TouristPlaceItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("location_lat")
    val locationLat: Double,

    @SerializedName("location_lng")
    val locationLng: Double,

    @SerializedName("address")
    val address: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("is_visited")
    val isVisited: Boolean,

    @SerializedName("average_rating")
    val averageRating: Double?,

    @SerializedName("total_reviews")
    val totalReviews: Int
)

data class TouristPlaceDetailResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: TouristPlaceDetail
)

data class TouristPlaceDetail(
    @SerializedName("place")
    val place: TouristPlaceItem,

    @SerializedName("reviews")
    val reviews: List<ReviewItem>,

    @SerializedName("user_visit")
    val userVisit: UserVisitData?
)

data class ReviewItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("user_image_url")
    val userImageUrl: String?,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("review_text")
    val reviewText: String?,

    @SerializedName("created_at")
    val createdAt: String
)

data class UserVisitData(
    @SerializedName("id")
    val id: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("visited_at")
    val visitedAt: String?
)

data class CheckinLocationResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: CheckinData?
)

data class CheckinData(
    @SerializedName("visit_id")
    val visitId: String,

    @SerializedName("place")
    val place: TouristPlaceItem,

    @SerializedName("xp_earned")
    val xpEarned: Int,

    @SerializedName("new_total_xp")
    val newTotalXp: Int
)

data class AddReviewResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ReviewItem?
)