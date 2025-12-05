package com.example.eventmanagement.autentikasi.supabase

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val is_verified: Boolean = false,
)