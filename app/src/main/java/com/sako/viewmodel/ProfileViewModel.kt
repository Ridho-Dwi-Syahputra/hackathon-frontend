package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.remote.response.ProfileData
import com.sako.data.repository.SakoRepository
import com.sako.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: SakoRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<Resource<ProfileData>>(Resource.Loading)
    val userProfile: StateFlow<Resource<ProfileData>> = _userProfile.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            repository.getProfile().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data.data?.let { profileData ->
                            _userProfile.value = Resource.Success(profileData)
                        } ?: run {
                            _userProfile.value = Resource.Error("Data profil tidak valid")
                        }
                    }
                    is Resource.Error -> {
                        _userProfile.value = Resource.Error(resource.error)
                    }
                    is Resource.Loading -> {
                        _userProfile.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }
}