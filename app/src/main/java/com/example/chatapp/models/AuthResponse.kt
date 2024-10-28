package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("UserId") val userId: Long,
    @SerializedName("Token") val token: Tokens
)

data class Tokens(
    @SerializedName("AccessToken") val accessToken: String,
    @SerializedName("RefreshToken") val refreshToken: String
)