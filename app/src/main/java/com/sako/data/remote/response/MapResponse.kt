package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

// Tourist Places List Response
data class TouristPlaceListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<TouristPlaceItem>
)

data class TouristPlaceItem(
    @SerializedName("tourist_place_id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("average_rating")
    val averageRating: Double = 0.0,
    
    @SerializedName("is_active")
    val isActive: Boolean = true,

    @SerializedName("is_visited")
    val isVisited: Boolean = false,

    @SerializedName("visited_at")
    val visitedAt: String?
)

// Tourist Place Detail Response
data class TouristPlaceDetailResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: TouristPlaceDetail
)

data class TouristPlaceDetail(
    @SerializedName("tourist_place_id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("average_rating")
    val averageRating: Double = 0.0,
    
    @SerializedName("is_scan_enabled")
    val isScanEnabled: Boolean,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
)

// Reviews Response
data class ReviewsResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ReviewsData
)

data class ReviewsData(
    @SerializedName("user_review")
    val userReview: ReviewItem?,

    @SerializedName("other_reviews") 
    val otherReviews: List<ReviewItem>,

    @SerializedName("pagination")
    val pagination: PaginationInfo
)

data class ReviewItem(
    @SerializedName("review_id")
    val id: String,

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("tourist_place_id")
    val touristPlaceId: String? = null,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("review_text")
    val reviewText: String?,

    @SerializedName("total_likes")
    val totalLikes: Int = 0,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    // Data user dari JOIN
    @SerializedName("user_full_name")
    val userName: String? = null,

    @SerializedName("user_image_url")
    val userImageUrl: String? = null,

    @SerializedName("is_liked_by_me")
    val isLikedByMe: Boolean = false
)

data class PaginationInfo(
    @SerializedName("current_page")
    val currentPage: Int,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_items")
    val totalItems: Int,

    @SerializedName("items_per_page")
    val itemsPerPage: Int
)

// Checkin Response
data class CheckinLocationResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: CheckinData?
)

data class CheckinData(
    @SerializedName("user_visit_id")
    val visitId: String,

    @SerializedName("tourist_place_id")
    val placeId: String,

    @SerializedName("place_name")
    val placeName: String,

    @SerializedName("visited_at")
    val visitedAt: String
)

// Add Review Response
data class AddReviewResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ReviewItem?
)

// Review Response (for add, update, delete)
data class ReviewResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ReviewResponseData?
)

data class ReviewResponseData(
    @SerializedName("review")
    val review: ReviewItem?,
    
    @SerializedName("tourist_place")
    val touristPlace: TouristPlaceItem?
)

// Review List Response
data class ReviewListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ReviewsData
)

// Visited Places Response
data class VisitedPlaceListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<VisitedPlaceItem>
)

data class VisitedPlaceItem(
    @SerializedName("tourist_place_id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("average_rating")
    val averageRating: Double = 0.0,

    @SerializedName("visited_at")
    val visitedAt: String,

    @SerializedName("status")
    val status: String
)

// Scan QR Response
data class ScanQRResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ScanQRData?
)

data class ScanQRData(
    @SerializedName("scan_success")
    val scanSuccess: Boolean,

    @SerializedName("tourist_place")
    val touristPlace: TouristPlaceItem?,

    @SerializedName("visited_at")
    val visitedAt: String?,
    
    @SerializedName("location_validation")
    val locationValidation: LocationValidation?,
    
    @SerializedName("reward_info")
    val rewardInfo: RewardInfo?
)

data class LocationValidation(
    @SerializedName("distance_meters")
    val distanceMeters: Double,
    
    @SerializedName("user_coordinates")
    val userCoordinates: Coordinates,
    
    @SerializedName("place_coordinates")
    val placeCoordinates: Coordinates,
    
    @SerializedName("within_radius")
    val withinRadius: Boolean
)

data class Coordinates(
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double
)

data class RewardInfo(
    @SerializedName("xp_earned")
    val xpEarned: Int,
    
    @SerializedName("total_xp")
    val totalXp: Int
)

// Toggle Like Response
data class ToggleLikeResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: LikeData?
)

data class LikeData(
    @SerializedName("review_id")
    val reviewId: String,

    @SerializedName("action")
    val action: String, // "liked" or "unliked"

    @SerializedName("total_likes")
    val totalLikes: Int,

    @SerializedName("is_liked_by_me")
    val isLikedByMe: Boolean,

    @SerializedName("user_info")
    val userInfo: UserInfo? = null // Make optional since backend doesn't send this
)

data class UserInfo(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("user_name")
    val userName: String
)