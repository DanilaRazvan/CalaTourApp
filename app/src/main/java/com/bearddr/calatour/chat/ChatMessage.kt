package com.bearddr.calatour.chat

data class ChatMessage(
    val sender: String = "",
    val text: String = "",
    val timestamp: String = "",
    var isImportant: Boolean = false
)