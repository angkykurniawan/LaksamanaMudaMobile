package com.example.eventmanagement.autentikasi.supabase

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseService {

    // Mengambil client dari Manager
    private val supabaseClient = SupabaseManager.client

    /**
     * Menyimpan data pengguna ke tabel 'users' di Supabase.
     */
    suspend fun registerUser(user: User): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Memanggil fungsi INSERT pada tabel "users"
            supabaseClient.postgrest["users"].insert(user)

            return@withContext Result.success("Akun berhasil dibuat.")

        } catch (e: Exception) {
            e.printStackTrace()
            // Mengembalikan pesan error yang relevan
            return@withContext Result.failure(e)
        }
    }
}