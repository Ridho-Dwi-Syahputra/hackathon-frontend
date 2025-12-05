package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sako.data.repository.MapRepository

/**
 * MapViewModelFactory - Factory untuk membuat MapViewModel dengan MapRepository
 * Sesuai dengan arsitektur yang diperbaiki dalam dokumentasi
 */
class MapViewModelFactory(
    private val mapRepository: MapRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(mapRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MapViewModelFactory? = null

        @JvmStatic
        fun getInstance(mapRepository: MapRepository): MapViewModelFactory {
            if (INSTANCE == null) {
                synchronized(MapViewModelFactory::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MapViewModelFactory(mapRepository)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}