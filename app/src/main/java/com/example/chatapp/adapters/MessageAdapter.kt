package com.example.chatapp.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.MessageInfo
import java.text.SimpleDateFormat
import java.util.Locale
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
            Log.i("MessageAdapter", "Binding message: $message")
            userNameTextView.text = message.userName
            messageTextView.text = message.text
            timeTextView.text = formatTime(message.sendingTime)
        }

        @SuppressLint("SimpleDateFormat")
        private fun formatTime(time: String?): String {
            if (time.isNullOrEmpty()) return ""

            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("HH:mm dd.MM", Locale.getDefault())
                outputFormat.timeZone = TimeZone.getDefault()

                val date = inputFormat.parse(time)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                Log.e("MessageAdapter", "Error parsing date: $time", e)
                ""
            }
        }
    }
}