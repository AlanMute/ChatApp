package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class AddMemberRequest(
    @SerializedName("chat_id") val chatId: Int,
    @SerializedName("members_ids") val membersIds: List<Int>
)