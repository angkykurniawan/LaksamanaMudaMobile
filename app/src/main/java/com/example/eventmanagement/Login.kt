package com.example.eventmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.eventmanagement.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val fragmentEmail = LoginEmail()
    private val fragmentNoHp = LoginNoHp()
    // Inisialisasi Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Inisialisasi Firebase ---
        auth = Firebase.auth

        // Tampilkan fragment Email saat Activity pertama kali dibuat
        if (savedInstanceState == null) {
            replaceFragment(fragmentEmail)
            updateToggleUI(true)
        }

        binding.btnEmail.setOnClickListener {
            replaceFragment(fragmentEmail)
            updateToggleUI(true)
        }

        binding.btnPhoneNumber.setOnClickListener {
            replaceFragment(fragmentNoHp)
            updateToggleUI(false)
        }
    }

    override fun onStart() {
        super.onStart()

        // =======================================================
        // PERBAIKAN: LOGOUT PAKSA JIKA ADA SESI AKTIF
        // =======================================================
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Jika ada pengguna yang login, LANGSUNG LOGOUT tanpa navigasi ke Home
            Log.d("Login", "Sesi aktif terdeteksi. Melakukan logout paksa.")
            auth.signOut()
            Toast.makeText(this, "Sesi login sebelumnya telah diakhiri. Silakan login kembali.", Toast.LENGTH_LONG).show()

            // Karena sign-out dipanggil, Activity ini akan me-refresh dan menampilkan tampilan Login
            // Kita tidak perlu memanggil navigateToHome() atau navigateToLogin() secara eksplisit di sini
        } else {
            // Jika tidak ada user, biarkan tampilan Login muncul.
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    private fun updateToggleUI(isEmailMode: Boolean) {
        val btnEmail = binding.btnEmail as MaterialButton
        val btnPhoneNumber = binding.btnPhoneNumber as MaterialButton

        // Fallback ke warna orange hardcode (#FF9800)
        val selectedColor = try { ContextCompat.getColor(this, R.color.Oren) } catch (e: Exception) { 0xFFFF9800.toInt() }
        val defaultWhite = ContextCompat.getColor(this, android.R.color.white)
        val black = ContextCompat.getColor(this, android.R.color.black)

        val strokeWidthPx = resources.displayMetrics.density.toInt() // 1dp dalam pixel
        val noStrokeWidth = 0

        if (isEmailMode) {
            btnEmail.setBackgroundColor(selectedColor)
            btnEmail.setTextColor(defaultWhite)
            btnEmail.strokeWidth = noStrokeWidth

            btnPhoneNumber.setBackgroundColor(defaultWhite)
            btnPhoneNumber.setTextColor(black)
            btnPhoneNumber.strokeWidth = strokeWidthPx
        } else {
            btnEmail.setBackgroundColor(defaultWhite)
            btnEmail.setTextColor(black)
            btnPhoneNumber.strokeWidth = strokeWidthPx

            btnPhoneNumber.setBackgroundColor(selectedColor)
            btnPhoneNumber.setTextColor(defaultWhite)
            btnPhoneNumber.strokeWidth = noStrokeWidth
        }
    }

    fun navigateToHome() {
        // Ganti HomeActivity::class.java dengan Activity utama aplikasi Anda
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}