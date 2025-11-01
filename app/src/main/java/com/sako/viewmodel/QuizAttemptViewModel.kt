package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.remote.DummyData
import com.sako.data.remote.request.QuizAnswerRequest
import com.sako.data.remote.response.OptionItem
import com.sako.data.remote.response.QuestionItem
import com.sako.data.remote.response.QuizAttemptData
import com.sako.data.remote.response.QuizResultData
import com.sako.data.remote.response.QuizStartResponse
import com.sako.data.remote.response.QuizSubmitResponse
import com.sako.data.repository.SakoRepository
import com.sako.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class QuizAttemptViewModel(
    private val repository: SakoRepository
) : ViewModel() {

    // ========== Quiz Attempt State ==========

    private val _quizState = MutableStateFlow<Resource<QuizStartResponse>>(Resource.Loading)
    val quizState: StateFlow<Resource<QuizStartResponse>> = _quizState.asStateFlow()

    private val _submitState = MutableStateFlow<Resource<QuizSubmitResponse>?>(null)
    val submitState: StateFlow<Resource<QuizSubmitResponse>?> = _submitState.asStateFlow()

    // ========== Quiz Progress State ==========

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<String, String>>(emptyMap()) // questionId -> optionId
    val selectedAnswers: StateFlow<Map<String, String>> = _selectedAnswers.asStateFlow()

    private val _isTimerPaused = MutableStateFlow(false)
    val isTimerPaused: StateFlow<Boolean> = _isTimerPaused.asStateFlow()

    private val _quizAttemptData = MutableStateFlow<QuizAttemptData?>(null)
    val quizAttemptData: StateFlow<QuizAttemptData?> = _quizAttemptData.asStateFlow()

    // ========== Functions ==========

    /**
     * Start a new quiz attempt
     * Fallback ke dummy data jika API gagal
     */
    fun startQuiz(levelId: String) {
        viewModelScope.launch {
            _quizState.value = Resource.Loading
            repository.startQuiz(levelId).collect { resource ->
                // Jika error (termasuk connection timeout), gunakan dummy data
                if (resource is Resource.Error) {
                    val dummyQuiz = DummyData.getDummyQuiz(levelId)
                    _quizState.value = Resource.Success(dummyQuiz)
                    _quizAttemptData.value = dummyQuiz.data
                    resetQuizState()
                } else {
                    _quizState.value = resource
                    
                    if (resource is Resource.Success) {
                        _quizAttemptData.value = resource.data.data
                        resetQuizState()
                    }
                }
            }
        }
    }

    /**
     * Get existing quiz attempt (untuk resume atau view hasil)
     */
    fun getQuizAttempt(attemptId: String) {
        viewModelScope.launch {
            _quizState.value = Resource.Loading
            repository.getQuizAttempt(attemptId).collect { resource ->
                _quizState.value = resource
                
                if (resource is Resource.Success) {
                    _quizAttemptData.value = resource.data.data
                }
            }
        }
    }

    /**
     * Submit quiz answers
     */
    fun submitQuiz() {
        val attemptData = _quizAttemptData.value ?: return
        
        viewModelScope.launch {
            _submitState.value = Resource.Loading
            
            // Build answers list
            val answers = attemptData.questions.map { question ->
                QuizAnswerRequest(
                    questionId = question.id,
                    optionId = _selectedAnswers.value[question.id],
                    answeredAt = if (_selectedAnswers.value.containsKey(question.id)) {
                        getCurrentTimestamp()
                    } else {
                        null
                    }
                )
            }
            
            repository.submitQuiz(attemptData.attemptId, answers).collect { resource ->
                _submitState.value = resource
                
                // Pause timer after submission
                if (resource is Resource.Success || resource is Resource.Error) {
                    _isTimerPaused.value = true
                }
            }
        }
    }

    /**
     * Select an answer for current question
     */
    fun selectAnswer(questionId: String, optionId: String) {
        val currentAnswers = _selectedAnswers.value.toMutableMap()
        currentAnswers[questionId] = optionId
        _selectedAnswers.value = currentAnswers
    }

    /**
     * Go to next question
     */
    fun nextQuestion() {
        val attemptData = _quizAttemptData.value ?: return
        val maxIndex = attemptData.questions.size - 1
        
        if (_currentQuestionIndex.value < maxIndex) {
            _currentQuestionIndex.value += 1
        }
    }

    /**
     * Go to previous question
     */
    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    /**
     * Jump to specific question by index
     */
    fun goToQuestion(index: Int) {
        val attemptData = _quizAttemptData.value ?: return
        val maxIndex = attemptData.questions.size - 1
        
        if (index in 0..maxIndex) {
            _currentQuestionIndex.value = index
        }
    }

    /**
     * Pause/Resume timer
     */
    fun toggleTimer() {
        _isTimerPaused.value = !_isTimerPaused.value
    }

    /**
     * Set timer paused state
     */
    fun setTimerPaused(paused: Boolean) {
        _isTimerPaused.value = paused
    }

    /**
     * Handle time up (auto submit)
     */
    fun onTimeUp() {
        _isTimerPaused.value = true
        submitQuiz()
    }

    /**
     * Reset quiz state for new attempt
     */
    private fun resetQuizState() {
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _isTimerPaused.value = false
        _submitState.value = null
    }

    /**
     * Clear submit state (untuk navigasi ke result screen)
     */
    fun clearSubmitState() {
        _submitState.value = null
    }

    /**
     * Clear all quiz data
     */
    fun clearQuizData() {
        _quizState.value = Resource.Loading
        _quizAttemptData.value = null
        resetQuizState()
    }

    // ========== Helper Functions ==========

    /**
     * Get current question
     */
    fun getCurrentQuestion(): QuestionItem? {
        val attemptData = _quizAttemptData.value ?: return null
        val index = _currentQuestionIndex.value
        return attemptData.questions.getOrNull(index)
    }

    /**
     * Get selected answer for current question
     */
    fun getSelectedAnswerForCurrentQuestion(): String? {
        val currentQuestion = getCurrentQuestion() ?: return null
        return _selectedAnswers.value[currentQuestion.id]
    }

    /**
     * Check if current question is answered
     */
    fun isCurrentQuestionAnswered(): Boolean {
        val currentQuestion = getCurrentQuestion() ?: return false
        return _selectedAnswers.value.containsKey(currentQuestion.id)
    }

    /**
     * Get total answered questions count
     */
    fun getAnsweredCount(): Int {
        return _selectedAnswers.value.size
    }

    /**
     * Get total unanswered questions count
     */
    fun getUnansweredCount(): Int {
        val attemptData = _quizAttemptData.value ?: return 0
        return attemptData.questions.size - _selectedAnswers.value.size
    }

    /**
     * Check if quiz can be submitted (minimal 1 pertanyaan dijawab)
     */
    fun canSubmitQuiz(): Boolean {
        return _selectedAnswers.value.isNotEmpty()
    }

    /**
     * Get option by ID from current question
     */
    fun getOptionById(optionId: String): OptionItem? {
        val currentQuestion = getCurrentQuestion() ?: return null
        return currentQuestion.options.find { it.id == optionId }
    }

    /**
     * Generate current timestamp in ISO format
     */
    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    /**
     * Get quiz result from submit state
     */
    fun getQuizResult(): QuizResultData? {
        val submitState = _submitState.value
        return if (submitState is Resource.Success) {
            submitState.data.data
        } else {
            null
        }
    }
}
