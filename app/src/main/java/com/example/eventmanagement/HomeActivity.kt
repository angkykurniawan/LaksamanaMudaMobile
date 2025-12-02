package com.example.eventmanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventmanagement.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.widget.Toast // Diperlukan untuk notifikasi

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth // Deklarasi FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth // Inisialisasi Auth

        // 1. Inisialisasi Binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Penanganan Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // AKTIVASI LOGOUT LANGSUNG SAAT LOGO DITEKAN
        // ===============================================
        // Asumsi: binding.logo adalah bagian dari ActivityHomeBinding atau dapat diakses
        // Jika binding.logo tidak ada, ganti dengan ID komponen yang ada di header Anda.
        binding.logo.setOnClickListener {
            logoutUser()
        }
    }

    /**
     * Melakukan proses logout pengguna dari Firebase.
     */




    fun logoutUser() {
        auth.signOut() // Panggil metode signOut()

        Toast.makeText(this, "Logout berhasil.", Toast.LENGTH_SHORT).show()

        // Alihkan pengguna kembali ke Login Activity
        val intent = Intent(this, Login::class.java)
        // Membersihkan stack agar halaman Home tidak bisa diakses dengan tombol back
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}