package com.sdu.sharewise.services

import com.google.firebase.Firebase
import com.google.firebase.functions.functions

object NotificationSenderService {
    private val functions = Firebase.functions

    fun sendNotification(tokens: List<String>, title: String, message: String) {
        val data = hashMapOf(
            "title" to title,
            "body" to message,
            "tokens" to tokens
        )

        functions
            .getHttpsCallable("sendNotifications")
            .call(data)
    }
}