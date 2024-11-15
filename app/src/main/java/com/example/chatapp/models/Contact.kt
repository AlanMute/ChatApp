package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("ID") val id: Int,
    @SerializedName("Login") val login: String?,
    @SerializedName("UserName") val userName: String?
)