package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceManager = PreferenceManager(this)
        val accessToken = preferenceManager.getAccessToken()

        if (accessToken != null) {
            // Токен существует, пользователь авторизован, переходим на главный экран
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Токена нет, перенаправляем на экран входа
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}