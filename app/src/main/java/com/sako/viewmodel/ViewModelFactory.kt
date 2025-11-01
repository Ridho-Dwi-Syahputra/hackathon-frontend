package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sako.data.repository.SakoRepository

class ViewModelFactory(
    private val repository: SakoRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(KuisViewModel::class.java) -> {
                KuisViewModel(repository) as T
            }
            modelClass.isAssignableFrom(QuizAttemptViewModel::class.java) -> {
                QuizAttemptViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(VideoViewModel::class.java) -> {
                VideoViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
