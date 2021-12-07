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
import com.bearddr.calatour.chat.remote.ChatApi
import com.bearddr.calatour.chat.remote.Requests
import com.bearddr.calatour.chat.remote.Responses
import com.bearddr.calatour.util.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var timer: Timer

    private val chatApi = ChatApi.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val messagesList = findViewById<RecyclerView>(R.id.recyclerView)
        val messageRef = findViewById<EditText>(R.id.etMessage)
        val usernameRef = findViewById<TextView>(R.id.tvUsername)
        usernameRef.text = UserInfo.username

        val sendButtonRef = findViewById<Button>(R.id.btnSend)
        sendButtonRef.setOnClickListener {

            Log.d("TAG", "onCreate: message sent")

            chatApi.sendMessage(
                header = "Bearer ${UserInfo.token}",
                body = Requests.SendMessage(messageRef.text.toString())
            ).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        messageRef.setText("")
                        messageRef.clearFocus()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) = Unit
            })


            chatMessageAdapter.insertMessage(
                ChatMessage(
                    usernameRef.text.toString(),
                    messageRef.text.toString(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())
                )
            )
        }

        chatMessageAdapter = ChatMessageAdapter()
        messagesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = chatMessageAdapter
        }

        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                chatApi.readMessages(
                    header = "Bearer ${UserInfo.token}"
                ).enqueue(object : Callback<Responses.ReadMessages> {
                    override fun onResponse(
                        call: Call<Responses.ReadMessages>,
                        response: Response<Responses.ReadMessages>
                    ) {
                        if (response.isSuccessful) {
                            val messages = response.body()!!.messages
                            Handler(mainLooper).post {
                                chatMessageAdapter.insertMessages(messages)
                                messagesList.layoutManager?.scrollToPosition(0)
                            }
                        }
                    }

                    override fun onFailure(call: Call<Responses.ReadMessages>, t: Throwable) = Unit
                })
            }
        }, 0, 2000)
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

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }
}