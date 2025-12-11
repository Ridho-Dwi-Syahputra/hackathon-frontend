package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sako.data.pref.UserPreference
import com.sako.di.Injection

class ProfileViewModelFactory(
    private val userPreference: UserPreference
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                val profileRepository = Injection.provideProfileRepository(userPreference)
                val authRepository = Injection.provideAuthRepository(userPreference)
                ProfileViewModel(profileRepository, authRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
