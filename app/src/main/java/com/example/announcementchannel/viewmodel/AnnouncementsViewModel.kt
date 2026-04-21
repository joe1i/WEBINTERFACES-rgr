package com.example.announcementchannel.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.announcementchannel.api.RetrofitClient
import com.example.announcementchannel.model.Announcement
import com.example.announcementchannel.model.ReactionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnnouncementsViewModel : ViewModel() {
    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadAnnouncements()
    }

    fun loadAnnouncements() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getAnnouncements()
                if (response.isSuccessful) {
                    _announcements.value = response.body()?.results ?: emptyList()
                    Log.d("API_SUCCESS", "Отримано оголошень: ${_announcements.value.size}")
                } else {
                    Log.e("API_ERROR", "Помилка сервера: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Критична помилка: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleReaction(announcementId: Int, reactionType: String) {
        viewModelScope.launch {
            try {
                val request = ReactionRequest(
                    announcement = announcementId,
                    reactionType = reactionType
                )

                val response = RetrofitClient.apiService.toggleReaction(request)

                if (response.isSuccessful) {
                    loadAnnouncements()
                } else {
                    Log.e("API_ERROR", "Помилка реакції: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Помилка з'єднання при відправці реакції: ${e.message}", e)
            }
        }
    }
}