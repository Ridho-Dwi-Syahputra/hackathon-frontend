package com.sako.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sako.data.repository.SakoRepository
import com.sako.data.repository.MapRepository
import com.sako.di.Injection

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(KuisViewModel::class.java) -> {
                val repository = Injection.provideRepository(context)
                KuisViewModel(repository) as T
            }
            modelClass.isAssignableFrom(QuizAttemptViewModel::class.java) -> {
                val repository = Injection.provideRepository(context)
                QuizAttemptViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                val repository = Injection.provideRepository(context)
                HomeViewModel(repository) as T
            }
            // ProfileViewModel menggunakan ProfileViewModelFactory sendiri
            modelClass.isAssignableFrom(VideoViewModel::class.java) -> {
                val repository = Injection.provideRepository(context)
                VideoViewModel(repository) as T
            }
            modelClass.isAssignableFrom(VideoCollectionViewModel::class.java) -> {
                val repository = Injection.provideRepository(context)
                VideoCollectionViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                val mapRepository = Injection.provideMapRepository(context)
                MapViewModel(mapRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
