package com.example.adminshopeease.Model

data class PushNotificationRequest(
    val token: String,
    val sender: String,
    val title: String,
    val body: String
)
