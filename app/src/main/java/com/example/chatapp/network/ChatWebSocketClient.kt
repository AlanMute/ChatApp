package com.example.chatapp.network

import android.content.Context
import android.util.Log
import com.example.chatapp.PreferenceManager
import com.example.chatapp.models.MessageInfo
import okhttp3.*
import org.json.JSONObject

class ChatWebSocketClient(
    private val context: Context,
    private val chatId: Int,
    private val onMessageReceived: (MessageInfo) -> Unit
) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val preferenceManager = PreferenceManager(context)

    fun connect() {
        val token = preferenceManager.getAccessToken()
        if (token != null) {
            val request = Request.Builder()
                .url("ws://193.124.33.25:8080/api/v1/messenger/connect?chat-id=$chatId")
                .addHeader("Authorization", "Bearer $token")
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.i("WebSocket", "Connected to chat $chatId")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.i("WebSocket", "Message received: $text")
                    val message = parseIncomingMessage(text)
                    onMessageReceived(message)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.i("WebSocket", "Error: ${t.message}")
                    if (response?.code == 401) {
                        refreshAccessTokenAndReconnect()
                    }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    webSocket.close(1000, null)
                    Log.d("WebSocket", "Closing: $code / $reason")
                }
            })
        } else {
            Log.e("WebSocket", "Token not found, unable to connect.")
        }
    }

    private fun refreshAccessTokenAndReconnect() {
        val newToken = RetrofitClient.refreshAccessToken(context, preferenceManager)
        if (newToken != null) {
            preferenceManager.saveAccessToken(newToken)
            connect() // Reconnect with new token
        } else {
            Log.e("WebSocket", "Token refresh failed, unable to reconnect.")
        }
    }

    fun sendMessage(text: String) {
        val json = JSONObject().apply {
            put("text", text)
        }
        webSocket?.send(json.toString())
    }

    fun close() {
        webSocket?.close(1000, "Chat closed")
    }

    private fun parseIncomingMessage(text: String): MessageInfo {
        val jsonObject = JSONObject(text)

        val msg = MessageInfo(
                id = jsonObject.getInt("id"),
        chatId = jsonObject.getInt("chatId"),
        senderId = jsonObject.getInt("senderId"),
        userName = jsonObject.getString("userName"),
        sendingTime = jsonObject.getString("sendingTime"),
        text = jsonObject.getString("text")
        )

        return MessageInfo(
            id = jsonObject.getInt("id"),
            chatId = jsonObject.getInt("chatId"),
            senderId = jsonObject.getInt("senderId"),
            userName = jsonObject.getString("userName"),
            sendingTime = jsonObject.getString("sendingTime"),
            text = jsonObject.getString("text")
        )
    }
}