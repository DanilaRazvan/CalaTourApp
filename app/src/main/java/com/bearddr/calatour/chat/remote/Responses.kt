package com.bearddr.calatour.chat.remote

import com.bearddr.calatour.chat.ChatMessage
import com.google.gson.annotations.SerializedName

sealed class Responses {

    data class Authentication(
        @SerializedName(value = "id")
        val id: Int,
        @SerializedName(value = "token")
        val token: String,
        @SerializedName(value = "display")
        val displayName: String
    )

    data class ReadMessages(
        val messages: List<ChatMessage>
    )

    data class ErrorDetails(
        val message: String
    )
}