package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class RefreshRequest(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("token") val token: String
)