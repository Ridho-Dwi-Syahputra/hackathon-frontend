package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

// Response untuk /api/home/dashboard
data class DashboardResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: DashboardData?
)

data class DashboardData(
    @SerializedName("user_stats")
    val userStats: UserStatsData,

    @SerializedName("recent_activities")
    val recentActivities: RecentActivitiesData,

    @SerializedName("popular_content")
    val popularContent: PopularContentData,

    @SerializedName("achievements")
    val achievements: List<Achievement>
)

data class RecentActivitiesData(
    @SerializedName("quiz_attempts")
    val quizAttempts: List<RecentQuizAttempt>
)

// User Statistics
data class UserStatsData(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("user_image_url")
    val userImageUrl: String?,

    @SerializedName("total_xp")
    val totalXp: Int,

    @SerializedName("level")
    val level: LevelInfo,

    @SerializedName("quiz_stats")
    val quizStats: QuizStats,

    @SerializedName("video_stats")
    val videoStats: VideoStats,

    @SerializedName("map_stats")
    val mapStats: MapStats,

    @SerializedName("achievements")
    val achievements: AchievementStats
)

data class LevelInfo(
    @SerializedName("current_level")
    val currentLevel: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("xp_current")
    val xpCurrent: Int,

    @SerializedName("xp_for_next_level")
    val xpForNextLevel: Int,

    @SerializedName("progress_percentage")
    val progressPercentage: Int
)

data class QuizStats(
    @SerializedName("total_attempts")
    val totalAttempts: Int,

    @SerializedName("completed")
    val completed: Int,

    @SerializedName("total_points")
    val totalPoints: String
)

data class VideoStats(
    @SerializedName("favorites")
    val favorites: Int,

    @SerializedName("collections")
    val collections: Int
)

data class MapStats(
    @SerializedName("places_visited")
    val placesVisited: Int,

    @SerializedName("reviews_written")
    val reviewsWritten: Int
)

data class AchievementStats(
    @SerializedName("badges_earned")
    val badgesEarned: Int
)

// Recent Activities
data class RecentQuizAttempt(
    @SerializedName("attempt_id")
    val attemptId: Int,

    @SerializedName("level_name")
    val levelName: String,

    @SerializedName("category_name")
    val categoryName: String,

    @SerializedName("points_earned")
    val pointsEarned: Int,

    @SerializedName("xp_earned")
    val xpEarned: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("is_completed")
    val isCompleted: Boolean
)

// Popular Content
data class PopularContentData(
    @SerializedName("videos")
    val videos: List<PopularVideo>,

    @SerializedName("places")
    val places: List<PopularPlace>
)

data class PopularVideo(
    @SerializedName("id")
    val id: String,

    @SerializedName("judul")
    val judul: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String?,

    @SerializedName("kategori")
    val kategori: String,

    @SerializedName("favorite_count")
    val favoriteCount: Int
)

data class PopularPlace(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("latitude")
    val latitude: String?,

    @SerializedName("longitude")
    val longitude: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("average_rating")
    val averageRating: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("review_count")
    val reviewCount: Int
)

// Achievements
data class Achievement(
    @SerializedName("badge_id")
    val badgeId: Int,

    @SerializedName("badge_name")
    val badgeName: String,

    @SerializedName("badge_description")
    val badgeDescription: String?,

    @SerializedName("badge_icon")
    val badgeIcon: String?,

    @SerializedName("earned_at")
    val earnedAt: String
)

// Response untuk /api/home/stats (jika digunakan terpisah)
data class UserStatsResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserStatsData?
)

// Response untuk /api/home/activities
data class RecentActivitiesResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<RecentQuizAttempt>?
)

// Response untuk /api/home/popular
data class PopularContentResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: PopularContentData?
)
