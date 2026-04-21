package com.example.announcementchannel.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val user: UserProfile,
    val token: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val gender: String?,
    @SerializedName("birth_date") val birthDate: String?,
    val password: String,
    @SerializedName("password_confirm") val passwordConfirm: String
)

data class ReactionRequest(
    val announcement: Int,
    @SerializedName("reaction_type") val reactionType: String
)

data class UserProfile(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val gender: String?,
    @SerializedName("birth_date") val birthDate: String?,
    val avatar: String?,
    val bio: String?,
    @SerializedName("created_at") val createdAt: String
)