package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.models.AuthResponse
import com.example.chatapp.models.User
import com.example.chatapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferenceManager = PreferenceManager(this)

        val editTextLogin = findViewById<EditText>(R.id.editTextLogin)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val login = editTextLogin.text.toString()
            val password = editTextPassword.text.toString()

            if (login.isNotEmpty() && password.isNotEmpty()) {
                val user = User(login, password)
                RetrofitClient.getInstance(this).signIn(user).enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                        if (response.isSuccessful) {
                            val authResponse = response.body()

                            // Извлекаем AccessToken и сохраняем его
                            val accessToken = authResponse?.token?.accessToken
                            if (accessToken != null) {
                                preferenceManager.saveToken(accessToken)
                                Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Access token is missing in response", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Please enter login and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}