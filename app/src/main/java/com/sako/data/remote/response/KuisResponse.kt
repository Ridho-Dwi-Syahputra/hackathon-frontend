package com.sako.data.remote.response

import com.google.gson.annotations.SerializedName

data class CategoryListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<CategoryItem>
)

data class CategoryItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("display_order")
    val displayOrder: Int,

    @SerializedName("progress")
    val progress: CategoryProgress?
)

data class CategoryProgress(
    @SerializedName("percent_completed")
    val percentCompleted: Double,

    @SerializedName("completed_levels_count")
    val completedLevelsCount: Int,

    @SerializedName("total_levels_count")
    val totalLevelsCount: Int
)

data class LevelListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: LevelListData
)

data class LevelListData(
    @SerializedName("category")
    val category: CategoryItem,

    @SerializedName("levels")
    val levels: List<LevelItem>
)

data class LevelItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("time_limit_seconds")
    val timeLimitSeconds: Int?,

    @SerializedName("base_xp")
    val baseXp: Int,

    @SerializedName("base_points")
    val basePoints: Int,

    @SerializedName("max_questions")
    val maxQuestions: Int?,

    @SerializedName("display_order")
    val displayOrder: Int,

    @SerializedName("progress")
    val progress: LevelProgress?,

    @SerializedName("pass_condition_type")
    val passConditionType: String,

    @SerializedName("pass_threshold")
    val passThreshold: Double
)

data class LevelProgress(
    @SerializedName("status")
    val status: String, // locked, unstarted, in_progress, completed

    @SerializedName("best_percent_correct")
    val bestPercentCorrect: Double,

    @SerializedName("best_score_points")
    val bestScorePoints: Int,

    @SerializedName("total_attempts")
    val totalAttempts: Int
)

data class QuizStartResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: QuizAttemptData
)

data class QuizAttemptData(
    @SerializedName("attempt_id")
    val attemptId: String,

    @SerializedName("level")
    val level: LevelItem,

    @SerializedName("questions")
    val questions: List<QuestionItem>,

    @SerializedName("duration_seconds")
    val durationSeconds: Int,

    @SerializedName("started_at")
    val startedAt: String
)

data class QuestionItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("points_correct")
    val pointsCorrect: Int,

    @SerializedName("display_order")
    val displayOrder: Int,

    @SerializedName("options")
    val options: List<OptionItem>,

    @SerializedName("points_wrong")
    val pointsWrong: Int
)

data class OptionItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("label")
    val label: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("display_order")
    val displayOrder: Int
)

data class QuizSubmitResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: QuizResultData
)

data class QuizResultData(
    @SerializedName("attempt_id")
    val attemptId: String,

    @SerializedName("score_points")
    val scorePoints: Int,

    @SerializedName("correct_count")
    val correctCount: Int,

    @SerializedName("wrong_count")
    val wrongCount: Int,

    @SerializedName("unanswered_count")
    val unansweredCount: Int,

    @SerializedName("percent_correct")
    val percentCorrect: Double,

    @SerializedName("xp_earned")
    val xpEarned: Int,

    @SerializedName("points_earned")
    val pointsEarned: Int,

    @SerializedName("is_passed")
    val isPassed: Boolean,

    @SerializedName("new_total_xp")
    val newTotalXp: Int,

    @SerializedName("badges_earned")
    val badgesEarned: List<BadgeItem>?
)