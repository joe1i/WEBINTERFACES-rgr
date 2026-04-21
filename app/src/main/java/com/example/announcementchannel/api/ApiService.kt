package com.example.announcementchannel.api

import com.example.announcementchannel.model.Announcement
import com.example.announcementchannel.model.LoginRequest
import com.example.announcementchannel.model.LoginResponse
import com.example.announcementchannel.model.RegisterRequest
import com.example.announcementchannel.model.ReactionRequest
import com.example.announcementchannel.model.PaginatedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register/")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @GET("api/announcements/")
    suspend fun getAnnouncements(
        @Query("ordering") ordering: String? = null,
        @Query("has_reaction") hasReaction: String? = null,
        @Query("min_likes") minLikes: Int? = null
    ): Response<PaginatedResponse<Announcement>>

    @GET("api/announcements/{id}/")
    suspend fun getAnnouncementDetail(@Path("id") id: Int): Response<Announcement>
    @POST("api/reactions/toggle/")
    suspend fun toggleReaction(@Body request: ReactionRequest): Response<Unit>

}