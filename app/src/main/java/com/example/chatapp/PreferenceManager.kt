package com.example.chatapp

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // Методы для сохранения данных
    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString("ACCESS_TOKEN", token).apply()
    }

    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString("REFRESH_TOKEN", token).apply()
    }

    fun saveUserId(userId: Long) {
        sharedPreferences.edit().putLong("USER_ID", userId).apply()
    }

    // Методы для получения данных
    fun getAccessToken(): String? {
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("REFRESH_TOKEN", null)
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong("USER_ID", -1)
    }

    // Метод для очистки данных при выходе из аккаунта
    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }
}