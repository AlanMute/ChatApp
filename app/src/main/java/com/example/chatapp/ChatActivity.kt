package com.example.chatapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapters.MessageAdapter
import com.example.chatapp.models.Chat
import com.example.chatapp.models.MessageInfo
import com.example.chatapp.network.ChatWebSocketClient
import com.example.chatapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var webSocketClient: ChatWebSocketClient
    private var chatId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatId = intent.getIntExtra("CHAT_ID", -1)
        if (chatId == -1) {
            Toast.makeText(this, "Ошибка: ID чата не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val toolbar = findViewById<Toolbar>(R.id.chatToolbar)
        setSupportActionBar(toolbar)

        val recyclerViewMessages = findViewById<RecyclerView>(R.id.recyclerViewMessages)
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(mutableListOf())
        recyclerViewMessages.adapter = messageAdapter

        loadChatInfo()
        loadChatMessages()

        setupWebSocket()

        val buttonSend = findViewById<Button>(R.id.buttonSendMessage)
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        buttonSend.setOnClickListener {
            val text = editTextMessage.text.toString()
            if (text.isNotEmpty()) {
                sendMessage(text)
                editTextMessage.text.clear()
            }
        }
    }

    private fun loadChatInfo() {
        RetrofitClient.getInstance(this).getChatInfo(chatId).enqueue(object : Callback<Chat> {
            override fun onResponse(call: Call<Chat>, response: Response<Chat>) {
                if (response.isSuccessful && response.body() != null) {
                    supportActionBar?.title = response.body()?.name
                } else {
                    Toast.makeText(this@ChatActivity, "Ошибка загрузки чата", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Chat>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadChatMessages() {
        RetrofitClient.getInstance(this).getChatMessages(chatId).enqueue(object : Callback<List<MessageInfo>> {
            override fun onResponse(call: Call<List<MessageInfo>>, response: Response<List<MessageInfo>>) {
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let { messages ->
                        messageAdapter.submitList(messages)
                        // Прокручиваем RecyclerView к последнему сообщению
                        val recyclerViewMessages = findViewById<RecyclerView>(R.id.recyclerViewMessages)
                        recyclerViewMessages.scrollToPosition(messages.size - 1)
                    }
                } else {
                    Toast.makeText(this@ChatActivity, "Не удалось загрузить сообщения", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<MessageInfo>>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupWebSocket() {
        webSocketClient = ChatWebSocketClient(this, chatId) { message ->
            runOnUiThread {
                messageAdapter.addMessage(message)
            }
        }
        webSocketClient.connect()
    }

    private fun sendMessage(text: String) {
        webSocketClient.sendMessage(text)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.close()
    }
}