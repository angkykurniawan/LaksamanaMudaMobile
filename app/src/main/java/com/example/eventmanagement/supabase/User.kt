package com.example.eventmanagement.supabase

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    // Diubah kembali ke String agar cocok dengan kolom database Anda
    val is_verified: String = "false"
)