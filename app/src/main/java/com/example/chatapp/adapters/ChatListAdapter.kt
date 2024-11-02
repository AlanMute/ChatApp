package com.example.chatapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.models.Chat
class ChatListAdapter(
    private var chats: List<Chat>,
    private val onChatClickListener: MainActivity
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    interface OnChatClickListener {
        fun onChatClick(chatId: Int)
    }

    fun updateChats(newChats: List<Chat>) {
        chats = newChats
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
        Log.i("as", "ASASQWQW $chat")
        holder.itemView.setOnClickListener {
            onChatClickListener.onChatClick(chat.id)
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatNameTextView: TextView = itemView.findViewById(R.id.textViewChatName)

        fun bind(chat: Chat) {
            chatNameTextView.text = chat.name
        }
    }
}