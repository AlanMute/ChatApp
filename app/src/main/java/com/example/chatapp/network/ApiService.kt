package com.example.chatapp.network

import com.example.chatapp.models.AuthResponse
import com.example.chatapp.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/v1/user/sign-in")
    fun signIn(@Body user: User): Call<AuthResponse>

    @POST("/api/v1/user/sign-up")
    fun signUp(@Body user: User): Call<Void>
}