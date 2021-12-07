package com.bearddr.calatour.chat.remote

import com.google.gson.annotations.SerializedName

sealed class Requests {

    data class Authentication(
        @SerializedName(value = "username")
        val username: String,
        @SerializedName(value = "password")
        val password: String
    )

    data class SendMessage(
        val message: String
    )
}