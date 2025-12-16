package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.remote.response.BadgeItem
import com.sako.data.remote.response.ProfileUserData
import com.sako.data.remote.response.UserStats
import com.sako.data.repository.AuthRepository
import com.sako.data.repository.ProfileRepository
import com.sako.data.model.LevelInfo
import com.sako.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userData: ProfileUserData? = null,
    val stats: UserStats? = null,
    val badges: List<BadgeItem>? = null,
    val levelInfo: LevelInfo? = null,
    val error: String? = null,
    val updateSuccess: Boolean = false,
    val updateMessage: String? = null,
    val notificationPreferences: com.sako.data.remote.request.NotificationPreferences? = null
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                updateSuccess = false,
                updateMessage = null
            )
            
            profileRepository.getProfile().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.data?.let { profileData ->
                            val levelInfo = calculateLevelInfo(profileData.user.totalXp)
                            _uiState.value = ProfileUiState(
                                isLoading = false,
                                userData = profileData.user,
                                stats = profileData.stats,
                                badges = profileData.badges,
                                levelInfo = levelInfo,
                                error = null
                            )
                        } ?: run {
                            _uiState.value = ProfileUiState(
                                isLoading = false,
                                error = "Data profil tidak valid"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.value = ProfileUiState(
                            isLoading = false,
                            error = resource.error
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun updateProfile(fullName: String, email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                updateSuccess = false,
                updateMessage = null
            )
            
            profileRepository.updateProfile(fullName, email).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Update local state with new data
                        val currentState = _uiState.value
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            updateSuccess = true,
                            updateMessage = resource.data?.message ?: "Profil berhasil diperbarui",
                            userData = currentState.userData?.copy(
                                fullName = fullName,
                                email = email
                            )
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            updateSuccess = false,
                            error = resource.error
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun updateProfileImage(imageFile: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                updateSuccess = false,
                updateMessage = null
            )
            
            profileRepository.updateProfileImage(imageFile).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val imageUrl = resource.data?.data?.user?.userImageUrl
                        val currentState = _uiState.value
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            updateSuccess = true,
                            updateMessage = resource.data?.message ?: "Foto profil berhasil diperbarui",
                            userData = currentState.userData?.copy(
                                userImageUrl = imageUrl
                            )
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            updateSuccess = false,
                            error = resource.error
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                updateSuccess = false,
                updateMessage = null
            )
            
            profileRepository.changePassword(currentPassword, newPassword).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            updateSuccess = true,
                            updateMessage = resource.data?.message ?: "Password berhasil diubah"
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            updateSuccess = false,
                            error = resource.error
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }

    fun clearUpdateStatus() {
        _uiState.value = _uiState.value.copy(
            updateSuccess = false,
            updateMessage = null,
            error = null
        )
    }

    suspend fun logout() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            authRepository.logout()
        } catch (e: Exception) {
            // Tetap logout dari lokal meskipun API gagal
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    fun loadNotificationPreferences() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            profileRepository.getNotificationPreferences().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            notificationPreferences = resource.data?.data?.notificationPreferences,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.error
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }
    
    fun updateNotificationPreferences(preferences: com.sako.data.remote.request.NotificationPreferences) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                updateSuccess = false,
                updateMessage = null
            )
            
            profileRepository.updateNotificationPreferences(preferences).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            notificationPreferences = preferences,
                            updateSuccess = true,
                            updateMessage = resource.data?.message ?: "Preferensi notifikasi berhasil diperbarui"
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            updateSuccess = false,
                            error = resource.error
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun calculateLevelInfo(totalXp: Int): LevelInfo {
        // Level thresholds - adjust based on your game design
        val levels = listOf(
            Pair(0, "Newbie"),           // 0-99 XP
            Pair(100, "Beginner"),       // 100-299 XP
            Pair(300, "Enthusiast"),     // 300-599 XP
            Pair(600, "Explorer"),       // 600-999 XP
            Pair(1000, "Adventurer"),    // 1000-1499 XP
            Pair(1500, "Expert"),        // 1500-2499 XP
            Pair(2500, "Master"),        // 2500-3999 XP
            Pair(4000, "Legend")         // 4000+ XP
        )

        var currentLevel = 1
        var levelName = "Newbie"
        var currentLevelXp = totalXp
        var nextLevelXp = 100

        for (i in levels.indices) {
            val (threshold, name) = levels[i]
            if (totalXp >= threshold) {
                currentLevel = i + 1
                levelName = name
                currentLevelXp = totalXp - threshold
                
                // Calculate next level XP
                if (i < levels.size - 1) {
                    nextLevelXp = levels[i + 1].first - threshold
                } else {
                    // Max level reached
                    nextLevelXp = currentLevelXp
                }
            } else {
                break
            }
        }

        val progressPercent = if (nextLevelXp > 0) {
            (currentLevelXp.toFloat() / nextLevelXp.toFloat()).coerceIn(0f, 1f)
        } else {
            1f
        }

        return LevelInfo(
            currentLevel = currentLevel,
            levelName = levelName,
            currentLevelXp = currentLevelXp,
            nextLevelXp = nextLevelXp,
            progressPercent = progressPercent
        )
    }
}