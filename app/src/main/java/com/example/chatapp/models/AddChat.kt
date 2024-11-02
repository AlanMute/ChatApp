package com.example.chatapp.models

data class AddChat(
    val name: String,
    val isDirect: Boolean,
    val membersIds: List<Int>
)