package com.example.chatapp.network

import LoggingInterceptor
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.chatapp.LoginActivity
import com.example.chatapp.PreferenceManager
import com.example.chatapp.models.RefreshRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://193.124.33.25:8080/api/v1/"

    private fun createOkHttpClient(context: Context): OkHttpClient {
        val preferenceManager = PreferenceManager(context)

        val loggingInterceptor = LoggingInterceptor()

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                var request = chain.request()
                val accessToken = preferenceManager.getAccessToken()

                // Добавляем токен в заголовок, если он доступен
                if (accessToken != null) {
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                }
                Log.i("CustomLog", "Loaded Access Token: $accessToken")

                // Выполняем запрос
                val response = chain.proceed(request)

                // Если токен просрочен, пробуем обновить его
                if (response.code == 401) {
                    response.close() // Закрываем предыдущий ответ перед повторным запросом
                    val newAccessToken = refreshAccessToken(context, preferenceManager)
                    if (newAccessToken != null) {
                        // Повторяем запрос с новым токеном
                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                        return@addInterceptor chain.proceed(newRequest)
                    } else {
                        // Если не удалось обновить токен, очищаем данные и переходим на экран входа
                        preferenceManager.clearData() // Очищаем сохраненные токены
                        val intent = Intent(context, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                }

                response
            }
            .build()
    }

    // Метод для обновления токена
    internal fun refreshAccessToken(context: Context, preferenceManager: PreferenceManager): String? {
        val refreshToken = preferenceManager.getRefreshToken() ?: return null
        val userId = preferenceManager.getUserId()

        val refreshService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val refreshResponse = refreshService.refreshToken(RefreshRequest(userId, refreshToken)).execute()

        return if (refreshResponse.isSuccessful) {
            val newAccessToken = refreshResponse.body()?.accessTocken
            if (newAccessToken != null) {
                preferenceManager.saveAccessToken(newAccessToken)
            }
            newAccessToken
        } else {
            null
        }
    }

    fun getInstance(context: Context): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


}