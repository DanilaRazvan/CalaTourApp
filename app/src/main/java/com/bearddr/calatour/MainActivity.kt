package com.bearddr.calatour

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.bearddr.calatour.chat.remote.ChatApi
import com.bearddr.calatour.chat.remote.Requests
import com.bearddr.calatour.chat.remote.Responses
import com.bearddr.calatour.util.UserInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val chatApi = ChatApi.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signInText = findViewById<TextView>(R.id.signInText)

        val usernameError = findViewById<TextView>(R.id.usernameError)
        val passwordError = findViewById<TextView>(R.id.passwordError)

        val usernameInput = findViewById<TextInputEditText>(R.id.usernameInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)

        usernameInput.doOnTextChanged { text, start, before, count ->
            usernameError.text = when (text.toString().length) {
                0 -> "Username cannot be empty"
                in 1..3 -> "Username too short"
                else -> ""
            }
        }
        passwordInput.doOnTextChanged { text, start, before, count ->
            passwordError.text = when (text.toString().length) {
                0 -> "Password cannot be empty"
                in 1..3 -> "password too short"
                else -> ""
            }
        }

        val button = findViewById<Button>(R.id.loginButton)
        button.setOnClickListener {
            val usernameVal = usernameInput.text.toString()
            val passwordVal = passwordInput.text.toString()

            chatApi.authenticate(Requests.Authentication(
                username = usernameVal,
                password = passwordVal
            )).enqueue(object : Callback<Responses.Authentication> {
                override fun onResponse(
                    call: Call<Responses.Authentication>,
                    response: Response<Responses.Authentication>
                ) {
                    Log.d("TAG", "onResponse: $response")

                    if (response.isSuccessful) {
                        val body = response.body()!!
                        UserInfo.username = body.displayName
                        UserInfo.token = body.token
                        UserInfo.userId = body.id
                        Log.d("TAG", "onResponse: $UserInfo")

                        val intent = Intent(this@MainActivity, OffersActivity::class.java)
                        startActivity(intent)
                    } else {
                        var errorDetails = Responses.ErrorDetails("Detailed error message is not available")
                        try {
                            if (response.errorBody() != null) {
                                val rawDetails = response.errorBody()!!.string()
                                val jsonParser = Gson()
                                errorDetails = jsonParser.fromJson(rawDetails, Responses.ErrorDetails::class.java)
                            }
                        } catch (e: Exception) {
                            errorDetails = Responses.ErrorDetails("Detailed error message could not be retrieved")
                        }

                        signInText.text = errorDetails.message
                        signInText.setTextColor(Color.RED)
                    }
                }

                override fun onFailure(call: Call<Responses.Authentication>, t: Throwable) {
                    signInText.text = "Server is unreachable"
                    signInText.setTextColor(Color.RED)
                }
            })
        }

        val globalLogoutButton = findViewById<Button>(R.id.globalLogoutButton)
        globalLogoutButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            chatApi.globalLogout(Requests.Authentication(username, password))
                .enqueue(object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext, "Global logout successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Some error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(applicationContext, "Internet connection issue", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}