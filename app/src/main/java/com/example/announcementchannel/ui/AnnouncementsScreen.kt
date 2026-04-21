package com.example.announcementchannel.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.announcementchannel.model.Announcement
import com.example.announcementchannel.viewmodel.AnnouncementsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(viewModel: AnnouncementsViewModel) {
    val announcements by viewModel.announcements.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Оголошення") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        if (isLoading && announcements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = announcements,
                    key = { announcement -> announcement.id }
                ) { announcement ->
                    AnnouncementCard(
                        announcement = announcement,
                        onReactionClick = { reactionType ->
                            viewModel.toggleReaction(announcement.id, reactionType)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    onReactionClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Заголовок
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "👤 ${announcement.authorName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "👁️ ${announcement.viewsCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val summary = announcement.reactionsSummary

                ReactionButton(
                    emoji = "👍",
                    count = summary?.like ?: 0,
                    isActive = announcement.userReaction == "like",
                    onClick = { onReactionClick("like") }
                )
                ReactionButton(
                    emoji = "❤️",
                    count = summary?.heart ?: 0,
                    isActive = announcement.userReaction == "heart",
                    onClick = { onReactionClick("heart") }
                )
                ReactionButton(
                    emoji = "🔥",
                    count = summary?.fire ?: 0,
                    isActive = announcement.userReaction == "fire",
                    onClick = { onReactionClick("fire") }
                )
                ReactionButton(
                    emoji = "😢",
                    count = summary?.sad ?: 0,
                    isActive = announcement.userReaction == "sad",
                    onClick = { onReactionClick("sad") }
                )
            }
        }
    }
}

@Composable
fun ReactionButton(emoji: String, count: Int, isActive: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent

    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.textButtonColors(containerColor = backgroundColor)
    ) {
        Text(text = "$emoji $count", style = MaterialTheme.typography.bodyLarge)
    }
}