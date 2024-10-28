package com.example.chatapp.models

data class RefreshRequest(
    val userId: Long,
    val token: String
)