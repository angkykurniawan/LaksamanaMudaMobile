package com.example.eventmanagement.autentikasi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventmanagement.R
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnUpdate: Button
    private var anggotaId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asumsi layout XML untuk tampilan kedua disebut 'activity_reset_password'
        setContentView(R.layout.activity_reset_password)

        anggotaId = intent.getStringExtra("ANGGOTA_ID")

        etNewPassword = findViewById(R.id.setNewPasswordField) // ID disinkronkan
        etConfirmPassword = findViewById(R.id.confirmNewPasswordField) // ID disinkronkan
        btnUpdate = findViewById(R.id.updatePasswordButton) // ID disinkronkan

        if (anggotaId == null) {
            Toast.makeText(this, "Error: Sesi reset password tidak valid.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        btnUpdate.setOnClickListener {
            updatePassword()
        }
    }

    private fun updatePassword() {
        val newPassword = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (newPassword.length < 9) { // Validasi minimal 9 karakter (dari RegisterActivity)
            etNewPassword.error = "Password minimal 9 karakter."
            return
        }
        if (newPassword != confirmPassword) {
            etConfirmPassword.error = "Konfirmasi password tidak cocok."
            return
        }

        btnUpdate.isEnabled = false
        val hashedPassword = hashPassword(newPassword)

        // 1. Update hanya field 'password' di node anggota terkait
        FirebaseDatabase.getInstance().getReference("anggota")
            .child(anggotaId!!)
            .child("password")
            .setValue(hashedPassword)
            .addOnSuccessListener {
                Toast.makeText(this, "Password berhasil diperbarui! Silakan Login.", Toast.LENGTH_LONG).show()

                // Navigasi ke halaman Login
                val intent = Intent(this, Login::class.java) // Asumsi Login Activity bernama Login
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { error ->
                btnUpdate.isEnabled = true
                Toast.makeText(this, "Gagal memperbarui password: ${error.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Fungsi utilitas untuk hashing password SHA-256 (Harus konsisten)
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}