package com.example.chatapp.network

import com.example.chatapp.models.AuthResponse
import com.example.chatapp.models.Chat
import com.example.chatapp.models.MessageInfo
import com.example.chatapp.models.RefreshRequest
import com.example.chatapp.models.User
import com.example.chatapp.models.UserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/api/v1/user/sign-in")
    fun signIn(@Body user: User): Call<AuthResponse>

    @POST("/api/v1/user/sign-up")
    fun signUp(@Body user: User): Call<Void>

    @POST("/api/v1/user/refresh/0")
    fun refreshToken(@Body refreshRequest: RefreshRequest): Call<String>

    @GET("/api/v1/chat/all")
    fun getChats(): Call<List<Chat>>

    @GET("/api/v1/chat/members/{id}")
    fun getChatMembers(@Path("id") chatId: Int): Call<List<UserInfo>>

    @GET("/api/v1/chat/messages")
    fun getMessages(
        @Query("chat-id") chatId: Int,
        @Query("page-id") pageId: Int
    ): Call<List<MessageInfo>>
}