package com.sako.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sako.data.pref.UserPreference
import com.sako.data.pref.dataStore
import com.sako.data.repository.SakoRepository
import com.sako.data.repository.MapRepository
import com.sako.di.Injection

/**
 * Unified ViewModelFactory - Single factory untuk semua ViewModel di aplikasi
 * Menyederhanakan dependency injection dan memudahkan maintenance
 */
class ViewModelFactory private constructor(
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Auth Module
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                val repository = Injection.provideAuthRepository(context)
                AuthViewModel(repository) as T
            }
            // Quiz/Kuis Module
            modelClass.isAssignableFrom(KuisViewModel::class.java) -> {
                val repository = Injection.provideRepository(context)
                KuisViewModel(repository) as T
            }
            modelClass.isAssignableFrom(QuizAttemptViewModel::class.java) -> {
                val repository = Injection.provideRepository(context)
                QuizAttemptViewModel(repository) as T
            }
            // Home Module
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                val homeRepository = Injection.provideHomeRepository(context)
                HomeViewModel(homeRepository) as T
            }
            // Video Module
            modelClass.isAssignableFrom(VideoViewModel::class.java) -> {
                val repository = Injection.provideRepository(context)
                VideoViewModel(repository) as T
            }
            modelClass.isAssignableFrom(VideoCollectionViewModel::class.java) -> {
                val repository = Injection.provideRepository(context)
                VideoCollectionViewModel(repository) as T
            }
            // Map Module
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                val mapRepository = Injection.provideMapRepository(context)
                MapViewModel(mapRepository) as T
            }
            // Profile Module
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                val userPreference = UserPreference.getInstance(context.dataStore)
                val profileRepository = Injection.provideProfileRepository(userPreference)
                val authRepository = Injection.provideAuthRepository(userPreference)
                ProfileViewModel(profileRepository, authRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        /**
         * Get singleton instance of ViewModelFactory
         * @param context Application context
         * @return ViewModelFactory instance
         */
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}
