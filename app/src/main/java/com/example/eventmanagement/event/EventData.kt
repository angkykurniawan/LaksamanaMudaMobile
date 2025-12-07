package com.example.eventmanagement.event

data class Event(
    val id: Int,
    val name: String,
    val status: String,
    val description: String,
    val price: String,
    val poster: String,
    val date: String
)