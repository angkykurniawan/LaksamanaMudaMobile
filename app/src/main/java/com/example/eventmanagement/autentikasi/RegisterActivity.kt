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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        database = FirebaseDatabase.getInstance().getReference("anggota")

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
        btnRegister.setOnClickListener {
            // Debugging: Pastikan tombol diklik
            Log.d(TAG, "Tombol Register ditekan.")
            handleRegisterClick()
        }
        btnBack.setOnClickListener { finish() }
    }

    private fun handleRegisterClick() {
        try {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            var isValid = true

            // Reset error messages sebelum validasi
            etUsername.error = null
            etEmail.error = null
            etPhone.error = null
            etPassword.error = null
            etConfirmPassword.error = null

            // --- VALIDASI DAN FEEDBACK VISUAL ---

            if (username.isEmpty()) { etUsername.error = "Nama wajib diisi"; isValid = false }
            if (email.isEmpty()) { etEmail.error = "Email wajib diisi"; isValid = false }
            if (phone.isEmpty()) { etPhone.error = "Nomor HP wajib diisi"; isValid = false }
            if (password.isEmpty()) { etPassword.error = "Password wajib diisi"; isValid = false }
            if (confirmPassword.isEmpty()) { etConfirmPassword.error = "Konfirmasi wajib diisi"; isValid = false }

            if (email.isNotEmpty() && !isValidEmail(email)) {
                etEmail.error = "Format email tidak valid"
                isValid = false
            }

            if (phone.isNotEmpty() && !phone.matches(Regex("^[0-9]+$"))) {
                etPhone.error = "Nomor HP hanya boleh angka"
                isValid = false
            }

            if (password.length > 0 && password.length <= 8) {
                etPassword.error = "Password minimal 9 karakter"
                isValid = false
            }

            if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                etConfirmPassword.error = "Konfirmasi password tidak cocok"
                isValid = false
            }

            // ------------------------------------

            if (!isValid) {
                // Modifikasi Pesan Gagal 1: Validasi Input
                Toast.makeText(this, "⚠️ Pendaftaran DIBATALKAN: Periksa kembali semua isian Anda.", Toast.LENGTH_LONG).show()
                Log.w(TAG, "Validasi gagal: Input pengguna tidak valid.")
                return
            }

            // Jika validasi sukses
            val hashedPassword = hashPassword(password)
            registerUserWithFirebase(username, email, phone, hashedPassword)

        } catch (e: Exception) {
            // Modifikasi Pesan Gagal 2: Crash/Error Internal
            Toast.makeText(this, "❌ ERROR INTERNAL: Terjadi kesalahan di aplikasi (Lihat logcat: $TAG).", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Exception tak terduga di handleRegisterClick: ${e.message}", e)
        }
    }

    private fun registerUserWithFirebase(
        username: String,
        email: String,
        phone: String,
        hashedPassword: String
    ) {
        val anggotaId = database.push().key

        if (anggotaId == null) {
            // Modifikasi Pesan Gagal 3: Gagal mendapatkan ID
            Toast.makeText(this, "❌ GAGAL FIREBASE: Tidak dapat membuat ID unik. Cek koneksi.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Gagal mendapatkan push key dari database.")
            return
        }

        val newUser = Anggota(
            id = anggotaId,
            name = username,
            email = email,
            phone = phone,
            password = hashedPassword,
            is_verified = "false"
        )

        btnRegister.isEnabled = false // Disable tombol saat proses dimulai
        Toast.makeText(this, "⏳ Proses: Mengirim data ke Firebase...", Toast.LENGTH_SHORT).show()

        database.child(anggotaId).setValue(newUser)
            .addOnSuccessListener {
                btnRegister.isEnabled = true
                // Modifikasi Pesan Sukses
                Toast.makeText(
                    this@RegisterActivity,
                    "✅ Pendaftaran BERHASIL! Data tersimpan di Firebase.",
                    Toast.LENGTH_LONG
                ).show()
                Log.i(TAG, "Data Anggota berhasil disimpan dengan ID: $anggotaId")

                // Navigasi ke Login Activity
                startActivity(
                    Intent(this@RegisterActivity, Login::class.java)
                )
                finish()
            }
            .addOnFailureListener { error ->
                btnRegister.isEnabled = true
                // Modifikasi Pesan Gagal 4: Error Koneksi/Otorisasi Firebase
                val errorMessage = error.message ?: "Kesalahan tak teridentifikasi."
                Toast.makeText(
                    this@RegisterActivity,
                    "❌ GAGAL FIREBASE: $errorMessage",
                    Toast.LENGTH_LONG
                ).show()
                // Gunakan Log.e untuk error kritis
                Log.e(TAG, "Firebase Save Failure: $errorMessage", error)

                // Saran: Periksa kembali Firebase Rules Anda
                //
            }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}