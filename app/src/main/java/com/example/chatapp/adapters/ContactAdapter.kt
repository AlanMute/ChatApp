package com.example.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.models.Contact
import android.util.Log

class ContactAdapter(
    private val onContactSelected: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private val contacts = mutableListOf<Contact>()

    fun submitList(contactList: List<Contact>) {
        Log.i("CustomLog", "USEEER: $contactList")
        contacts.clear()
        contacts.addAll(contactList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount() = contacts.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox = itemView.findViewById<CheckBox>(R.id.checkboxContact)

        fun bind(contact: Contact) {
            checkBox.text = contact.login + " (" + contact.userName + ")"
            checkBox.isChecked = false
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onContactSelected(contact.id, isChecked)
            }
        }
    }
}