package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapters.ChatListAdapter
import com.example.chatapp.models.Chat
import com.example.chatapp.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatListAdapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Устанавливаем Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Обработчик нажатия на FloatingActionButton
        val fabAddChat = findViewById<FloatingActionButton>(R.id.fabAddChat)
        fabAddChat.setOnClickListener {
            // Открываем экран создания чата
            startActivity(Intent(this, AddChatActivity::class.java))
        }

        // Настраиваем RecyclerView для отображения списка чатов
        recyclerView = findViewById(R.id.recyclerViewChats)
        recyclerView.layoutManager = LinearLayoutManager(this)

        chatListAdapter = ChatListAdapter(listOf())
        recyclerView.adapter = chatListAdapter

        loadChats() // Загружаем список чатов
    }

    private fun loadChats() {
        val apiService = RetrofitClient.getInstance(this)
        apiService.getChats().enqueue(object : Callback<List<Chat>> {
            override fun onResponse(call: Call<List<Chat>>, response: Response<List<Chat>>) {
                if (response.isSuccessful && response.body() != null) {
                    chatListAdapter.updateChats(response.body()!!)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load chats", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Chat>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                // Открываем профиль пользователя
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_settings -> {
                // Открываем настройки приложения
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}