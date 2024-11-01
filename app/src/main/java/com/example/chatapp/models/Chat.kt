package com.example.chatapp.models

data class Chat(
    val id: Int,
    val isDirect: Boolean,
    val name: String,
    val ownerId: Int
)