package com.example.chatapp.models

import com.google.gson.annotations.SerializedName

data class AccessTocken (
    @SerializedName("access_token") val accessTocken: String
)