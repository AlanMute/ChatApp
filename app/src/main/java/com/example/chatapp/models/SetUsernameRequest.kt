package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class SetUsernameRequest(
    @SerializedName("username") val username: String
)