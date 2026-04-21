package com.example.announcementchannel.viewmodel

import com.example.announcementchannel.model.UserProfile

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
}