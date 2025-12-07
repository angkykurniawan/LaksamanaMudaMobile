package com.example.eventmanagement.event

data class Event(
    val id: Int,
    val name: String,
    val date: String,
    val priceRange: String,
    val posterResource: Int, // Resource ID dari drawable
    val status: String // "Upcoming", "Pending", "History"
)