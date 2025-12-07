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
import java.security.MessageDigest
import java.util.regex.Pattern

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
        setContentView(R.layout.activity_register)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.et_username)
        etEmail = findViewById(R.id.et_email)
        etPhone = findViewById(R.id.et_phone)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)

        btnRegister = findViewById(R.id.btn_register)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener { handleRegisterClick() }
        btnBack.setOnClickListener { finish() }
    }

    private fun handleRegisterClick() {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        var isValid = true

        // ✅ Validasi kosong
        if (username.isEmpty()) { etUsername.error = "Nama wajib diisi"; isValid = false }
        if (email.isEmpty()) { etEmail.error = "Email wajib diisi"; isValid = false }
        if (phone.isEmpty()) { etPhone.error = "Nomor HP wajib diisi"; isValid = false }
        if (password.isEmpty()) { etPassword.error = "Password wajib diisi"; isValid = false }
        if (confirmPassword.isEmpty()) { etConfirmPassword.error = "Konfirmasi wajib diisi"; isValid = false }

        // ✅ Validasi format email
        if (email.isNotEmpty() && !isValidEmail(email)) {
            etEmail.error = "Format email tidak valid"
            isValid = false
        }

        // ✅ Nomor HP hanya angka
        if (phone.isNotEmpty() && !phone.matches(Regex("^[0-9]+$"))) {
            etPhone.error = "Nomor HP hanya boleh angka"
            isValid = false
        }

        // ✅ Password HARUS lebih dari 8 karakter (minimal 9)
        if (password.length <= 8) {
            etPassword.error = "Password minimal 9 karakter"
            isValid = false
        }

        // ✅ Konfirmasi password harus sama
        if (password != confirmPassword) {
            etConfirmPassword.error = "Konfirmasi password tidak cocok"
            isValid = false
        }

        if (!isValid) {
            Log.w(TAG, "Validasi gagal")
            return
        }

        // ✅ Hash password
        val hashedPassword = hashPassword(password)

        val newUser = User(
            name = username,
            email = email,
            phone = phone,
            password = hashedPassword,
            is_verified = "false"
        )

        lifecycleScope.launch {
            btnRegister.isEnabled = false

            val result = supabaseService.registerUser(newUser)

            btnRegister.isEnabled = true

            result.onSuccess {
                Toast.makeText(
                    this@RegisterActivity,
                    "Pendaftaran berhasil!",
                    Toast.LENGTH_LONG
                ).show()

                startActivity(
                    Intent(this@RegisterActivity, Login::class.java)
                )
                finish()
            }.onFailure { error ->
                Toast.makeText(
                    this@RegisterActivity,
                    "Gagal: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ✅ VALIDASI FORMAT EMAIL
    private fun isValidEmail(email: String): Boolean {
        val emailPattern =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }

    // ✅ HASH PASSWORD SHA-256
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
