package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.UserInfo

class MembersAdapter : RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    private val members = mutableListOf<UserInfo>()

    fun submitList(newMembers: List<UserInfo>) {
        members.clear()
        members.addAll(newMembers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int = members.size

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val memberInfoTextView: TextView = itemView.findViewById(R.id.textViewMemberInfo)

        fun bind(user: UserInfo) {
            memberInfoTextView.text = "${user.login}${if (user.userName.isNotEmpty()) " - ${user.userName}" else ""}"
        }
    }
}