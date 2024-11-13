package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class AddChat(
    @SerializedName("name") val name: String,
    @SerializedName("is_direct") val isDirect: Boolean,
    @SerializedName("members_ids") val membersIds: List<Int>
)