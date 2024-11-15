import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import android.util.Log

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Логируем информацию о запросе
        Log.i("CustomLog", "Request URL: ${request.url}")
        Log.i("CustomLog", "Request Headers: ${request.headers["Authorization"]}")
        Log.i("CustomLog", "Request Method: ${request.method}")

        // Попробуем логировать тело запроса, если оно есть
        request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            Log.i("CustomLog", "Request Body: ${buffer.readUtf8()}")
        }

        // Выполняем запрос и перехватываем ответ
        val response = chain.proceed(request)
        val responseBody = response.body
        val content = responseBody?.string() ?: ""

        // Логируем ответ
        Log.i("CustomLog", "Response Code: ${response.code}")
        Log.i("CustomLog", "Response Headers: ${response.headers}")
        Log.i("CustomLog", "Response Body: $content")

        // Восстанавливаем тело ответа для дальнейшей обработки
        return response.newBuilder()
            .body(content.toResponseBody(responseBody?.contentType()))
            .build()
    }
}
