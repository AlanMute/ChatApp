package com.example.chatapp

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("JWT_TOKEN", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove("JWT_TOKEN").apply()
    }
}