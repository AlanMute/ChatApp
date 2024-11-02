package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class MessageInfo(
    @SerializedName("ChatId")val chatId: Int,
    @SerializedName("ID")val id: Int,
    @SerializedName("SenderId")val senderId: Int,
    @SerializedName("SendingTime")val sendingTime: String,
    @SerializedName("Text")val text: String,
    @SerializedName("UserName")val userName: String
)