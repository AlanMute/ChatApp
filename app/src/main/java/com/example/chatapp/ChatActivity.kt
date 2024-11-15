package com.example.chatapp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapters.MembersAdapter
import com.example.chatapp.adapters.MessageAdapter
import com.example.chatapp.models.AddContact
import com.example.chatapp.models.AddMemberRequest
import com.example.chatapp.models.Chat
import com.example.chatapp.models.Contact
import com.example.chatapp.models.MessageInfo
import com.example.chatapp.models.UserInfo
import com.example.chatapp.network.ChatWebSocketClient
import com.example.chatapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private var currentPage = 0
    private var isLoading = false
    private var hasMorePages = true
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var webSocketClient: ChatWebSocketClient
    private var chatInfo: Chat? = null
    private var ownerInfo: Contact? = null
    private val chatMembers = mutableListOf<UserInfo>()
    private val selectedContacts = mutableSetOf<Int>()
    private var chatId: Int = -1
    private var isOwner: Boolean = false

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        preferenceManager = PreferenceManager(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatId = intent.getIntExtra("CHAT_ID", -1)
        if (chatId == -1) {
            Toast.makeText(this, "Ошибка: ID чата не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val toolbar = findViewById<Toolbar>(R.id.chatToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerViewMessages = findViewById<RecyclerView>(R.id.recyclerViewMessages)
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(mutableListOf())
        recyclerViewMessages.adapter = messageAdapter

        setupRecyclerViewPagination(recyclerViewMessages)
        loadChatInfo()
        loadChatMembers()
        loadChatMessages(0)

        setupWebSocket()

        val buttonSend = findViewById<Button>(R.id.buttonSendMessage)
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        buttonSend.setOnClickListener {
            val text = editTextMessage.text.toString()
            if (text.isNotEmpty()) {
                sendMessage(text)
                editTextMessage.text.clear()
            }
        }
    }

    private fun setupRecyclerViewPagination(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                if (!isLoading && hasMorePages && layoutManager.findFirstVisibleItemPosition() == 0) {
                    loadChatMessages(currentPage + 1)
                }
            }
        })
    }

    private fun loadChatInfo() {
        RetrofitClient.getInstance(this).getChatInfo(chatId).enqueue(object : Callback<Chat> {
            override fun onResponse(call: Call<Chat>, response: Response<Chat>) {
                if (response.isSuccessful && response.body() != null) {
                    chatInfo = response.body()
                    Log.i("chatInfo", "$chatInfo")
                    supportActionBar?.title = chatInfo?.name
                    isOwner = chatInfo?.ownerId?.toLong() == preferenceManager.getUserId()
                    invalidateOptionsMenu()

                    loadOwnerInfo(chatInfo?.ownerId ?: -1)
                } else {
                    Toast.makeText(this@ChatActivity, "Ошибка загрузки чата", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Chat>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadChatMembers() {
        RetrofitClient.getInstance(this).getChatMembers(chatId).enqueue(object : Callback<List<UserInfo>> {
            override fun onResponse(call: Call<List<UserInfo>>, response: Response<List<UserInfo>>) {
                if (response.isSuccessful && response.body() != null) {
                    chatMembers.clear()
                    chatMembers.addAll(response.body()!!)
                } else {
                    Toast.makeText(this@ChatActivity, "Ошибка загрузки участников", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserInfo>>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Ошибка загрузки участников", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadChatMessages(pageId: Int) {
        if (isLoading || !hasMorePages) return

        isLoading = true
        RetrofitClient.getInstance(this).getChatMessages(chatId, pageId).enqueue(object : Callback<List<MessageInfo>> {
            override fun onResponse(call: Call<List<MessageInfo>>, response: Response<List<MessageInfo>>) {
                isLoading = false
                if (response.isSuccessful && response.body() != null) {
                    val messages = response.body()!!
                    if (messages.isEmpty()) {
                        hasMorePages = false
                    } else {
                        messageAdapter.addMessagesToStart(messages)
                        currentPage = pageId
                    }
                } else {
                    Toast.makeText(this@ChatActivity, "Не удалось загрузить сообщения", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<MessageInfo>>, t: Throwable) {
                isLoading = false
                Toast.makeText(this@ChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun loadOwnerInfo(ownerId: Int) {
        Log.i("OwnerId", "$ownerId")
        RetrofitClient.getInstance(this).getContact(ownerId).enqueue(object : Callback<Contact> {
            override fun onResponse(call: Call<Contact>, response: Response<Contact>) {
                if (response.isSuccessful && response.body() != null) {
                    ownerInfo = response.body() // Сохраняем данные о владельце
                    Log.i("ownerInfo", "$response.body()")
                }
            }

            override fun onFailure(call: Call<Contact>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupWebSocket() {

        webSocketClient = ChatWebSocketClient(this, chatId) { message ->
            runOnUiThread {
                messageAdapter.addMessage(message)
                scrollToBottom()
            }
        }
        webSocketClient.connect()
    }

    private fun sendMessage(text: String) {
        webSocketClient.sendMessage(text)
        scrollToBottom()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.close()
    }

    private fun scrollToBottom() {
        val recyclerViewMessages = findViewById<RecyclerView>(R.id.recyclerViewMessages)
        recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val settingsItem = menu?.findItem(R.id.action_settings)
        settingsItem?.isVisible = isOwner // Показываем только для владельца
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                showChatSettingsDialog()
                true
            }
            R.id.info -> {
                showChatInfoDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showChatSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_chat_settings, null)
        val recyclerViewContacts = dialogView.findViewById<RecyclerView>(R.id.recyclerViewContacts)
        val buttonAddMembers = dialogView.findViewById<Button>(R.id.buttonAddMembers)
        val buttonDeleteChat = dialogView.findViewById<Button>(R.id.buttonDeleteChat)
        val buttonAddContact = dialogView.findViewById<Button>(R.id.buttonAddContact)

        recyclerViewContacts.layoutManager = LinearLayoutManager(this)
        val contactAdapter = ContactAdapter { contactId, isChecked ->
            if (isChecked) selectedContacts.add(contactId) else selectedContacts.remove(contactId)
        }
        recyclerViewContacts.adapter = contactAdapter

        loadNonMembers(contactAdapter) // Загружаем контакты, которые не в чате

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Настройки чата")
            .setNegativeButton("Отмена", null)
            .create()

        buttonAddMembers.setOnClickListener {
            addMembersToChat()
            dialog.dismiss()
        }

        buttonDeleteChat.setOnClickListener {
            deleteChat()
            dialog.dismiss()
        }

        buttonAddContact.setOnClickListener {
            showAddContactDialog(contactAdapter)
        }

        dialog.show()
    }

    private fun showChatInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_chat_info, null)
        val chatTitleTextView = dialogView.findViewById<TextView>(R.id.textViewChatTitle)
        val creatorInfoTextView = dialogView.findViewById<TextView>(R.id.textViewCreatorInfo)
        val recyclerViewMembers = dialogView.findViewById<RecyclerView>(R.id.recyclerViewMembers)

        recyclerViewMembers.layoutManager = LinearLayoutManager(this)
        val membersAdapter = MembersAdapter()
        recyclerViewMembers.adapter = membersAdapter

        // Устанавливаем заголовок чата
        chatInfo?.let { chat ->
            chatTitleTextView.text = chat.name
        }

        // Устанавливаем информацию о владельце
        ownerInfo?.let { owner ->
            creatorInfoTextView.text = "Создатель: ${owner.login}${if (!owner.userName.isNullOrEmpty()) " - ${owner.userName}" else ""}"
        }

        // Устанавливаем список участников
        membersAdapter.submitList(chatMembers)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Информация о чате")
            .setNegativeButton("Закрыть", null)
            .show()
    }


    private fun loadNonMembers(contactAdapter: ContactAdapter) {
        RetrofitClient.getInstance(this).getChatMembers(chatId).enqueue(object : Callback<List<UserInfo>> {
            override fun onResponse(call: Call<List<UserInfo>>, response: Response<List<UserInfo>>) {
                if (response.isSuccessful && response.body() != null) {
                    val members = response.body()!!.map { it.id }.toSet()
                    RetrofitClient.getInstance(this@ChatActivity).getContacts().enqueue(object : Callback<List<Contact>> {
                        override fun onResponse(call: Call<List<Contact>>, response: Response<List<Contact>>) {
                            if (response.isSuccessful && response.body() != null) {
                                val nonMembers = response.body()!!.filter { it.id !in members }
                                contactAdapter.submitList(nonMembers)
                            }
                        }

                        override fun onFailure(call: Call<List<Contact>>, t: Throwable) {
                            Toast.makeText(this@ChatActivity, "Ошибка загрузки контактов", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onFailure(call: Call<List<UserInfo>>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Ошибка загрузки участников", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addMembersToChat() {
        val addMemberRequest = AddMemberRequest(chatId = chatId, membersIds = selectedContacts.toList())
        RetrofitClient.getInstance(this).addMembers(addMemberRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadChatMembers()
                    Toast.makeText(this@ChatActivity, "Участники добавлены", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ChatActivity, "Ошибка добавления участников", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteChat() {
        RetrofitClient.getInstance(this).deleteChat(chatId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ChatActivity, "Чат удален", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ChatActivity, "Ошибка удаления чата", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddContactDialog(contactAdapter: ContactAdapter) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val editTextContactLogin = dialogView.findViewById<EditText>(R.id.editTextContactLogin)
        val buttonAddContactDialog = dialogView.findViewById<Button>(R.id.buttonAddContactDialog)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Добавить контакт")
            .setNegativeButton("Отмена", null)
            .create()

        buttonAddContactDialog.setOnClickListener {
            val contactLogin = editTextContactLogin.text.toString()
            if (contactLogin.isNotEmpty()) {
                addNewContact(contactLogin) { success ->
                    if (success) loadNonMembers(contactAdapter) // Обновляем список доступных для добавления контактов
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Введите логин", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun addNewContact(contactLogin: String, callback: (Boolean) -> Unit) {
        val apiService = RetrofitClient.getInstance(this)
        val addContactRequest = AddContact(contact_login = contactLogin)

        apiService.addContact(addContactRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ChatActivity, "Контакт добавлен", Toast.LENGTH_SHORT).show()
                    callback(true)
                } else {
                    Toast.makeText(this@ChatActivity, "Ошибка добавления контакта", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        })
    }
}