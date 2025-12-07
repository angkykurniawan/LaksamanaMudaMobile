package com.example.eventmanagement.autentikasi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.eventmanagement.R
import com.example.eventmanagement.supabase.SupabaseService
import com.example.eventmanagement.supabase.User
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

// ==========================================================
// SKELETON: Ganti dengan kelas LoginActivity Anda yang sebenarnya!
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "Berhasil Daftar. Silakan masuk!", Toast.LENGTH_LONG).show()
    }
}
// ==========================================================


class RegisterActivity : AppCompatActivity() {

    private val TAG = "REGISTER_DEBUG"

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private lateinit var btnRegister: Button
    private lateinit var btnBack: ImageButton

    private val supabaseService = SupabaseService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate START")

        setContentView(R.layout.activity_register)
        Log.d(TAG, "Layout SET")

        try {
            initViews()
            Log.d(TAG, "initViews COMPLETE")
            setupListeners()
            Log.d(TAG, "Listeners COMPLETE")

            btnRegister.text = "Register"
        } catch (e: Exception) {
            // Log error eksplisit jika crash di inisialisasi View (NPE)
            Log.e(TAG, "FATAL VIEW INITIALIZATION ERROR: ${e.message}", e)
            Toast.makeText(this, "Aplikasi mengalami kegagalan inisialisasi. Cek Logcat!", Toast.LENGTH_LONG).show()
            // Sangat penting untuk melempar error agar kita melihat stack trace penuh
            throw e
        }
    }

    private fun initViews() {
        // Deklarasi View - PASTIKAN ID INI 100% SESUAI DENGAN XML
        etUsername = findViewById(R.id.et_username)
        etEmail = findViewById(R.id.et_email)
        etPhone = findViewById(R.id.et_phone)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)

        btnRegister = findViewById<Button>(R.id.btn_register)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            handleRegisterClick()
        }
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun handleRegisterClick() {
        Log.d(TAG, "Register Clicked: START")

        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        var isValid = true

        // ... (Validasi kolom kosong dan password cocok) ...

        if (username.isEmpty()) { etUsername.error = "Nama pengguna wajib diisi"; isValid = false }
        if (email.isEmpty()) { etEmail.error = "Email wajib diisi"; isValid = false }
        if (phone.isEmpty()) { etPhone.error = "Nomor HP wajib diisi"; isValid = false }
        if (password.isEmpty()) { etPassword.error = "Kata sandi wajib diisi"; isValid = false }
        if (confirmPassword.isEmpty()) { etConfirmPassword.error = "Konfirmasi wajib diisi"; isValid = false }
        if (password.isNotEmpty() && password != confirmPassword) {
            etConfirmPassword.error = "Kata sandi tidak cocok"
            isValid = false
        }

        if (!isValid) {
            Log.w(TAG, "Validasi gagal. Pendaftaran dibatalkan.")
            return
        }

        // Membuat objek User
        val newUser = User(
            name = username,
            email = email,
            phone = phone,
            password = password,
            is_verified = "false"
        )

        Log.d(TAG, "Attempting registration for user: $email")

        // Melakukan operasi jaringan di Coroutine
        lifecycleScope.launch {
            btnRegister.isEnabled = false

            val result = supabaseService.registerUser(newUser)

            btnRegister.isEnabled = true

            result.onSuccess {
                Log.i(TAG, "Pendaftaran berhasil!")
                Toast.makeText(this@RegisterActivity, "Pendaftaran Berhasil! Silakan masuk.", Toast.LENGTH_LONG).show()

                // Navigasi ke Halaman Login
                val intent = Intent(this@RegisterActivity, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()

            }.onFailure { error ->
                val errorMessage = error.message ?: "Terjadi kesalahan jaringan atau database."
                Toast.makeText(this@RegisterActivity, "Pendaftaran Gagal: ${errorMessage}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Register Failed: ${errorMessage}", error)
            }
        }
    }
}