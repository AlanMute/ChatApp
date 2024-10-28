package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем PreferenceManager для проверки токена
        val preferenceManager = PreferenceManager(this)
        val accessToken = preferenceManager.getAccessToken() // Изменили getToken на getAccessToken

        if (accessToken != null) {
            // Если токен существует, переходим на главный экран
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Если токена нет, переходим на экран входа
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Завершаем SplashActivity
    }
}