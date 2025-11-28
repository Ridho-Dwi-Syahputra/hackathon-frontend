package com.sako.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sako.data.remote.response.AuthResponse
import com.sako.data.repository.SakoRepository
import com.sako.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: SakoRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<AuthResponse>?>(null)
    val registerState: StateFlow<Resource<AuthResponse>?> = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow<Resource<AuthResponse>?>(null)
    val loginState: StateFlow<Resource<AuthResponse>?> = _loginState.asStateFlow()

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            repository.register(fullName, email, password).collect { resource ->
                _registerState.value = resource
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password).collect { resource ->
                _loginState.value = resource
            }
        }
    }

    fun clearRegisterState() {
        _registerState.value = null
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}