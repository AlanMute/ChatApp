package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class ContactInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val login: String?,
    @SerializedName("userName") val userName: String?
)