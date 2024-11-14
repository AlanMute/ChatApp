package com.example.chatapp.network

import com.example.chatapp.models.AccessTocken
import com.example.chatapp.models.AddChat
import com.example.chatapp.models.AddContact
import com.example.chatapp.models.AddMemberRequest
import com.example.chatapp.models.AuthResponse
import com.example.chatapp.models.Chat
import com.example.chatapp.models.Contact
import com.example.chatapp.models.MessageInfo
import com.example.chatapp.models.RefreshRequest
import com.example.chatapp.models.User
import com.example.chatapp.models.UserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/api/v1/user/sign-in")
    fun signIn(@Body user: User): Call<AuthResponse>

    @POST("/api/v1/user/sign-up")
    fun signUp(@Body user: User): Call<Void>

    @POST("/api/v1/user/refresh")
    fun refreshToken(@Body refreshRequest: RefreshRequest): Call<AccessTocken>

    @GET("/api/v1/chat/all")
    fun getChats(): Call<List<Chat>>

    @GET("/api/v1/chat/members/{id}")
    fun getChatMembers(@Path("id") chatId: Int): Call<List<UserInfo>>

    @POST("/api/v1/chat")
    fun createChat(@Body addChat: AddChat): Call<Void>

    @GET("/api/v1/contact/all")
    fun getContacts(): Call<List<Contact>>

    // Добавление нового контакта
    @POST("/api/v1/contact")
    fun addContact(@Body contact: AddContact): Call<Void>

    @GET("/api/v1/chat/{id}")
    fun getChatInfo(@Path("id") chatId: Int): Call<Chat>

    @GET("/api/v1/chat/messages")
    fun getChatMessages(
        @Query("chat-id") chatId: Int,
        @Query("page-id") pageId: Int = 0 // по умолчанию первая страница
    ): Call<List<MessageInfo>>

    @POST("/api/v1/chat/add/members")
    fun addMembers(@Body addMemberRequest: AddMemberRequest): Call<Void>

    @DELETE("/api/v1/chat/{id}")
    fun deleteChat(@Path("id") chatId: Int): Call<Void>
}