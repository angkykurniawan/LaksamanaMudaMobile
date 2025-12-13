package com.example.eventmanagement.team

data class Crew(
    // ID Unik dari Firebase (Push Key)
    val id: String? = null,
    // Data Utama Kru
    val name: String? = null,
    val birthDate: String? = null, // Format: dd/MM/yyyy
    val joinDate: String? = null, // Format: dd/MM/yyyy
    val role: String? = null, // Contoh: Crew
    val email: String? = null,
    val phone: String? = null // Tambahkan field phone dari struktur tabel
)