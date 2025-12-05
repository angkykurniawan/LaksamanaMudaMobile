package com.example.eventmanagement.autentikasi

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.eventmanagement.R
import com.example.eventmanagement.autentikasi.supabase.SupabaseService
import com.example.eventmanagement.autentikasi.supabase.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // View Declarations
    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnBack: ImageButton

    // Inisialisasi service
//    private val supabaseService = SupabaseService()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
//
//        initViews()
//        setupListeners()
//    }
//
//    private fun initViews() {
//        // !!! Jika aplikasi crash di sini, salah satu ID di bawah ini TIDAK ADA di XML !!!
//        etUsername = findViewById(R.id.et_username)
//        etEmail = findViewById(R.id.et_email)
//        etPhone = findViewById(R.id.et_phone)
//        etPassword = findViewById(R.id.et_password)
//        etConfirmPassword = findViewById(R.id.et_confirm_password)
//        btnRegister = findViewById(R.id.btn_register)
//        btnBack = findViewById(R.id.btn_back)
//    }
//
//    private fun setupListeners() {
//        btnRegister.setOnClickListener {
//            handleRegisterClick()
//        }
//        btnBack.setOnClickListener {
//            onBackPressed()
//        }
//    }
//
//    private fun handleRegisterClick() {
//        val username = etUsername.text.toString().trim()
//        val email = etEmail.text.toString().trim()
//        val phone = etPhone.text.toString().trim()
//        val password = etPassword.text.toString()
//        val confirmPassword = etConfirmPassword.text.toString()
//        var isValid = true
//
//        // --- 0. Reset Error ---
//        etUsername.error = null
//        etEmail.error = null
//        etPhone.error = null
//        etPassword.error = null
//        etConfirmPassword.error = null
//
//        // --- 1. Validasi Kolom Kosong ---
//        if (username.isEmpty()) { etUsername.error = "Nama pengguna wajib diisi"; isValid = false }
//        if (email.isEmpty()) { etEmail.error = "Email wajib diisi"; isValid = false }
//        if (phone.isEmpty()) { etPhone.error = "Nomor HP wajib diisi"; isValid = false }
//        if (password.isEmpty()) { etPassword.error = "Kata sandi wajib diisi"; isValid = false }
//        if (confirmPassword.isEmpty()) { etConfirmPassword.error = "Konfirmasi wajib diisi"; isValid = false }
//
//        // --- 2. Validasi Kecocokan Password ---
//        if (password.isNotEmpty() && password != confirmPassword) {
//            etConfirmPassword.error = "Kata sandi tidak cocok"
//            isValid = false
//        }
//
//        if (!isValid) return
//
//        // --- 3. Panggil Supabase Service ---
//        val newUser = User(
//            name = username,
//            email = email,
//            phone = phone,
//            password = password,
//            is_verified = false
//        )
//
//        lifecycleScope.launch {
//            // Jika Anda menggunakan kode DEBUG MODE di SupabaseManager,
//            // CRASH FATAL akan terjadi di baris di bawah saat tombol ditekan.
//
//            val result = supabaseService.registerUser(newUser)
//
//            result.onSuccess { message ->
//                Toast.makeText(this@RegisterActivity, "Pendaftaran Berhasil! $message", Toast.LENGTH_LONG).show()
//
//                // Navigasi ke Halaman Login
//                val intent = Intent(this@RegisterActivity, Login::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()
//
//            }.onFailure { error ->
//                Toast.makeText(this@RegisterActivity, "Pendaftaran Gagal: ${error.message}", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
}