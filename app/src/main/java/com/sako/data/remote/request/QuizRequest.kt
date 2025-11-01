package com.sako.data.remote.request

import com.google.gson.annotations.SerializedName

// Start Quiz / Checkin Quiz Request
data class CheckinQuizRequest(
    @SerializedName("level_id")
    val levelId: String
)

// Submit Quiz Request
data class SubmitQuizRequest(
    @SerializedName("attempt_id")
    val attemptId: String,

    @SerializedName("answers")
    val answers: List<QuizAnswerRequest>
)

// Quiz Answer Item
data class QuizAnswerRequest(
    @SerializedName("question_id")
    val questionId: String,

    @SerializedName("option_id")
    val optionId: String?,

    @SerializedName("answered_at")
    val answeredAt: String? = null
)