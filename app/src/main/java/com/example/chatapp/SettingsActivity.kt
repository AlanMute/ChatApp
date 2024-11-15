package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.chatapp.models.Contact
import com.example.chatapp.models.SetUsernameRequest
import com.example.chatapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var buttonSaveUsername: Button
    private lateinit var buttonLogout: Button
    private lateinit var preferenceManager: PreferenceManager
    private var currentUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Инициализация Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = "Настройки"

        // Инициализация элементов интерфейса
        preferenceManager = PreferenceManager(this)
        editTextUsername = findViewById(R.id.editTextUsername)
        buttonSaveUsername = findViewById(R.id.buttonSaveUsername)
        buttonLogout = findViewById(R.id.buttonLogout)

        // Загрузка текущего имени пользователя
        loadCurrentUsername()

        // Обработчик сохранения имени пользователя
        buttonSaveUsername.setOnClickListener {
            val newUsername = editTextUsername.text.toString().trim()
            if (newUsername.isNotEmpty() && newUsername != currentUsername) {
                saveUsername(newUsername)
            } else {
                Toast.makeText(this, "Введите новое имя или измените текущее", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработчик выхода
        buttonLogout.setOnClickListener {
            logout()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadCurrentUsername() {
        val userId = preferenceManager.getUserId().toInt()
        val apiService = RetrofitClient.getInstance(this)

        apiService.getContact(userId).enqueue(object : Callback<Contact> {
            override fun onResponse(call: Call<Contact>, response: Response<Contact>) {
                if (response.isSuccessful && response.body() != null) {
                    currentUsername = response.body()?.userName
                    editTextUsername.setText(currentUsername) // Устанавливаем имя в поле
                } else {
                    Toast.makeText(this@SettingsActivity, "Ошибка загрузки имени пользователя", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Contact>, t: Throwable) {
                Toast.makeText(this@SettingsActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUsername(username: String) {
        val apiService = RetrofitClient.getInstance(this)
        val request = SetUsernameRequest(username)

        apiService.setUsername(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    currentUsername = username
                    Toast.makeText(this@SettingsActivity, "Имя пользователя обновлено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SettingsActivity, "Ошибка при обновлении имени", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@SettingsActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logout() {
        preferenceManager.clearData() // Удаляем данные пользователя
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}