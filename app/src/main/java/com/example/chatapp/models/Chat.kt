package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class Chat(
    @SerializedName("id") val id: Int,
    @SerializedName("IsDirect") val isDirect: Boolean,
    @SerializedName("Name") val name: String,
    @SerializedName("OwnerId") val ownerId: Int
)