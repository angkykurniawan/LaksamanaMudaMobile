package com.example.eventmanagement.event

data class Event(
    val id: String? = null,
    val name: String? = null,
    val status: String? = null,
    val description: String? = null,
    val priceRange: String? = null,
    val posterUrl: String? = null,
    val date: String? = null
)