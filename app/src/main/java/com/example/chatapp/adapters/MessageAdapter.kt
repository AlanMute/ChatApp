package com.example.chatapp.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.ChatMessage
import com.example.chatapp.models.MessageInfo
import java.text.SimpleDateFormat
import java.util.Locale
import java.text.ParseException
import java.util.TimeZone

class MessageAdapter(private val messages: MutableList<MessageInfo>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    fun addMessage(message: MessageInfo) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun submitList(newMessages: List<MessageInfo>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.textViewUserName)
        private val messageTextView: TextView = itemView.findViewById(R.id.textViewMessage)
        private val timeTextView: TextView = itemView.findViewById(R.id.textViewTime)

        fun bind(message: MessageInfo) {
            Log.i("AAAAAAAAAA", "username: $message")
            userNameTextView.text = message.userName
            messageTextView.text = message.text
            timeTextView.text = formatTime(message.sendingTime)
        }

        @SuppressLint("SimpleDateFormat")
        private fun formatTime(time: String?): String {
            if (time.isNullOrEmpty()) return ""

            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault()

            return try {
                // Проверяем, какой формат времени у нас есть
                if (time.contains("T") && time.endsWith("Z")) {
                    // Формат из истории чата: 2024-11-02T01:02:02.465371Z
                    val inputFormatHistory = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                    inputFormatHistory.timeZone = TimeZone.getTimeZone("UTC")
                    val date = inputFormatHistory.parse(time)
                    outputFormat.format(date!!)
                } else {
                    // Формат из WebSocket: 2024-11-02 08:49:46.69871592 +0000 UTC m=+1092445.529513174
                    // Удаляем лишние части строки и обрезаем до миллисекунд
                    val simplifiedTime = time.split(" ")[0] + " " + time.split(" ")[1].substring(0, 15)
                    val inputFormatWebSocket = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault())
                    inputFormatWebSocket.timeZone = TimeZone.getTimeZone("UTC")
                    val date = inputFormatWebSocket.parse(simplifiedTime)
                    outputFormat.format(date!!)
                }
            } catch (e: ParseException) {
                // Если парсинг не удался, возвращаем пустую строку
                ""
            }
        }
    }
}