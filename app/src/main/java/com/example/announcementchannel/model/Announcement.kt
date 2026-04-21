package com.example.announcementchannel.model

import com.google.gson.annotations.SerializedName

data class ReactionsSummary(
    val like: Int?,
    val heart: Int?,
    val fire: Int?,
    val sad: Int?
)

data class Announcement(
    val id: Int,
    val title: String,
    val content: String,
    val image: String?,
    @SerializedName("author_username") val authorUsername: String,
    @SerializedName("author_name") val authorName: String,
    @SerializedName("author_avatar") val authorAvatar: String?,
    @SerializedName("views_count") val viewsCount: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("reactions_summary") val reactionsSummary: ReactionsSummary?,
    @SerializedName("user_reaction") val userReaction: String?
)

data class PaginatedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)