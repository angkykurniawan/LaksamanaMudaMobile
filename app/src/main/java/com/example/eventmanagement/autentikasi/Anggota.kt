package com.example.eventmanagement.autentikasi

data class Anggota(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val is_verified: String? = "false",
    val otp_temp: String? = null
)