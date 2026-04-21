package com.example.announcementchannel.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var currentOrdering by mutableStateOf("-created_at")
        private set
    var currentReactionFilter by mutableStateOf<String?>(null)
        private set
    var minLikesFilter by mutableStateOf<Int?>(null)
        private set

    private val viewedIds = mutableSetOf<Int>()

    init {
        loadAnnouncements()
    }

    fun loadAnnouncements() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getAnnouncements(
                    ordering = currentOrdering,
                    hasReaction = currentReactionFilter,
                    minLikes = minLikesFilter
                )
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

    fun changeSort(newOrdering: String) {
        currentOrdering = newOrdering
        _announcements.value = emptyList()
        loadAnnouncements()
    }

    fun toggleReactionFilter(reaction: String) {
        currentReactionFilter = if (currentReactionFilter == reaction) null else reaction
        _announcements.value = emptyList()
        loadAnnouncements()
    }
    fun sortByReaction(reactionType: String) {
        currentOrdering = when (reactionType) {
            "like" -> "-likes_count"
            "fire" -> "-fire_count"
            "heart" -> "-heart_count"
            "sad" -> "-sad_count"
            else -> "-created_at"
        }

        _announcements.value = emptyList()
        loadAnnouncements()
    }

    fun updateMinLikesFilter(minLikes: Int?) {
        minLikesFilter = if (minLikesFilter == minLikes) null else minLikes
        _announcements.value = emptyList()
        loadAnnouncements()
    }

    fun markAsViewed(announcementId: Int) {
        if (viewedIds.contains(announcementId)) return
        viewedIds.add(announcementId)

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAnnouncementDetail(announcementId)

                if (response.isSuccessful) {
                    val updatedAnnouncement = response.body()

                    if (updatedAnnouncement != null) {
                        _announcements.value = _announcements.value.map { oldAnnouncement ->
                            if (oldAnnouncement.id == announcementId) {
                                updatedAnnouncement
                            } else {
                                oldAnnouncement
                            }
                        }
                    }
                    Log.d("API_SUCCESS", "Перегляд зараховано та оновлено в UI: $announcementId")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Помилка перегляду: ${e.message}")
            }
        }
    }

    fun toggleReaction(announcementId: Int, reactionType: String) {
        viewModelScope.launch {
            try {
                val request = ReactionRequest(announcementId, reactionType)
                val response = RetrofitClient.apiService.toggleReaction(request)
                if (response.isSuccessful) {
                    loadAnnouncements()
                } else {
                    Log.e("API_ERROR", "Помилка реакції: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Помилка з'єднання: ${e.message}", e)
            }
        }
    }
}