package com.example.chatapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.models.AddChat
import com.example.chatapp.models.AddContact
import com.example.chatapp.models.Contact
import com.example.chatapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog

class AddChatActivity : AppCompatActivity() {

    private lateinit var contactAdapter: ContactAdapter
    private val selectedContacts = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chat)

        val editTextChatName = findViewById<EditText>(R.id.editTextChatName)
        val buttonCreateChat = findViewById<Button>(R.id.buttonCreateChat)
        val buttonAddContact = findViewById<Button>(R.id.buttonAddContact)
        val recyclerViewContacts = findViewById<RecyclerView>(R.id.recyclerViewContacts)

        recyclerViewContacts.layoutManager = LinearLayoutManager(this)
        contactAdapter = ContactAdapter { contactId, isChecked ->
            if (isChecked) selectedContacts.add(contactId) else selectedContacts.remove(contactId)
        }
        recyclerViewContacts.adapter = contactAdapter

        loadContacts()

        buttonCreateChat.setOnClickListener {
            val chatName = editTextChatName.text.toString()
            if (chatName.isNotEmpty() && selectedContacts.isNotEmpty()) {
                createChat(chatName, selectedContacts.toList())
            } else {
                Toast.makeText(this, "Введите название и выберите участников", Toast.LENGTH_SHORT).show()
            }
        }

        buttonAddContact.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun loadContacts() {
        RetrofitClient.getInstance(this).getContacts().enqueue(object : Callback<List<Contact>> {
            override fun onResponse(call: Call<List<Contact>>, response: Response<List<Contact>>) {
                if (response.isSuccessful && response.body() != null) {
                    val uniqueContacts = response.body()?.distinctBy { it.id } ?: emptyList()
                    contactAdapter.submitList(uniqueContacts)
                } else {
                    Toast.makeText(this@AddChatActivity, "Ошибка загрузки контактов", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Contact>>, t: Throwable) {
                Toast.makeText(this@AddChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createChat(chatName: String, members: List<Int>) {
        val addChatRequest = AddChat(name = chatName, isDirect = false, membersIds = members)
        RetrofitClient.getInstance(this).createChat(addChatRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddChatActivity, "Чат создан!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddChatActivity, "Ошибка создания чата", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val editTextContactLogin = dialogView.findViewById<EditText>(R.id.editTextContactLogin)
        val buttonAddContact = dialogView.findViewById<Button>(R.id.buttonAddContactDialog)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Добавить контакт")
            .setNegativeButton("Отмена", null)
            .create()

        buttonAddContact.setOnClickListener {
            val contactLogin = editTextContactLogin.text.toString()
            if (contactLogin.isNotEmpty()) {
                addNewContact(contactLogin)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Введите логин", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun addNewContact(contactLogin: String) {
        val apiService = RetrofitClient.getInstance(this)
        val addContactRequest = AddContact(contact_login = contactLogin)

        apiService.addContact(addContactRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddChatActivity, "Контакт добавлен", Toast.LENGTH_SHORT).show()
                    loadContacts() // Обновляем список контактов
                } else {
                    Toast.makeText(this@AddChatActivity, "Ошибка добавления контакта", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}