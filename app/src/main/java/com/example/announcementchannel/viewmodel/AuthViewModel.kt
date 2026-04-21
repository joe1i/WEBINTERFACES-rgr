package com.example.announcementchannel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.announcementchannel.api.RetrofitClient
import com.example.announcementchannel.model.LoginRequest
import com.example.announcementchannel.model.RegisterRequest
import com.example.announcementchannel.api.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val request = LoginRequest(email = email, password = pass)
                val response = RetrofitClient.apiService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    TokenManager.token = loginResponse.token
                    _authState.value = AuthState.Success(loginResponse.user)
                } else {
                    _authState.value = AuthState.Error("Помилка входу. Перевірте email та пароль.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Помилка з'єднання: ${e.localizedMessage}")
            }
        }
    }

    fun register(
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        gender: String,
        birthDate: String,
        pass: String,
        passConfirm: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val finalGender = gender.ifBlank { null }
                val finalBirthDate = birthDate.ifBlank { null }

                val request = RegisterRequest(
                    username = username,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    gender = finalGender,
                    birthDate = finalBirthDate,
                    password = pass,
                    passwordConfirm = passConfirm
                )

                val response = RetrofitClient.apiService.register(request)

                if (response.isSuccessful) {
                    login(email, pass)
                } else {
                    _authState.value = AuthState.Error("Помилка реєстрації: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Помилка мережі: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}