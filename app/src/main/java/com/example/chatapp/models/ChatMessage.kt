package com.example.chatapp.models

data class ChatMessage(
    val id: Int,
    val chatId: Int,
    val senderId: Int,
    val userName: String,
    val sendingTime: String,
    val text: String
)