package com.example.chatapp.models

data class MessageInfo(
    val chatId: Int,
    val id: Int,
    val senderId: Int,
    val sendingTime: String,
    val text: String,
    val userName: String
)