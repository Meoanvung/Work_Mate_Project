package com.example.tempapp.main.message

import androidx.compose.ui.graphics.Color

data class ChatContact(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Color,
    val preview: String,
    val unreadCount: Int = 0
)

data class ChatBubble(
    val text: String,
    val time: String,
    val isMine: Boolean = false,
    val isTeamworkPlaceholder: Boolean = false
)
