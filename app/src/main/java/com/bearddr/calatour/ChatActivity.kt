package com.bearddr.calatour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearddr.calatour.chat.ChatMessage
import com.bearddr.calatour.chat.ChatMessageAdapter
import com.bearddr.calatour.util.UserInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatActivity : AppCompatActivity() {

    private val dataSource = mutableListOf<ChatMessage>()
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val messagesList = findViewById<RecyclerView>(R.id.recyclerView)
        val messageRef = findViewById<EditText>(R.id.etMessage)
        val usernameRef = findViewById<TextView>(R.id.tvUsername)
        usernameRef.text = UserInfo.username

        val sendButtonRef = findViewById<Button>(R.id.btnSend)
        sendButtonRef.setOnClickListener {
            chatMessageAdapter.insertMessage(
                ChatMessage(
                    usernameRef.text.toString(),
                    messageRef.text.toString(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())
                )
            )
            messagesList.layoutManager?.scrollToPosition(0)
            messageRef.setText("")
            messageRef.clearFocus()
        }

        chatMessageAdapter = ChatMessageAdapter()
        messagesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = chatMessageAdapter
        }

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Handler(mainLooper).post {
                    chatMessageAdapter.insertMessage(
                        ChatMessage(
                            "Computer",
                            "Automated message ${++counter}",
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())
                        )
                    )
                    messagesList.layoutManager?.scrollToPosition(0)
                }
            }
        }, 0, 5000)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.chatMenuDesign1 -> {
                chatMessageAdapter.setDesign(1)
            }

            R.id.chatMenuDesign2 -> {
                chatMessageAdapter.setDesign(2)
            }

            R.id.chatMenuBothDesigns -> {
                chatMessageAdapter.setDesign(0)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}