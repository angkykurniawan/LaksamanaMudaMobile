package com.example.eventmanagement // Ganti dengan package Anda

import android.os.Bundle
// Pastikan Anda mengimpor MaterialButton jika Anda menggunakannya
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.eventmanagement.databinding.ActivityLoginBinding // Ganti ke nama binding Activity container Anda (sebelumnya ActivityLoginBinding)

// HAPUS BARIS YANG ERROR: private var Button.strokeWidth: Int
// Jika binding Anda ActivityLoginBinding, pastikan Activity Anda adalah Login, bukan LoginContainerActivity

class Login: AppCompatActivity() { // Menggunakan nama Activity container

    private lateinit var binding: ActivityLoginBinding
    // Menggunakan nama kelas Fragment: LoginEmail dan LoginNoHp
    private val fragmentEmail = LoginEmail()
    private val fragmentNoHp = LoginNoHp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    private fun updateToggleUI(isEmailMode: Boolean) {
        // Casting tombol ke MaterialButton agar bisa mengakses strokeWidth
        val btnEmail = binding.btnEmail as MaterialButton
        val btnPhoneNumber = binding.btnPhoneNumber as MaterialButton

        // Pastikan R.color.Oren terdefinisi dan dapat diakses
        val selectedColor = ContextCompat.getColor(this, R.color.Oren)
        val defaultWhite = ContextCompat.getColor(this, android.R.color.white)
        val black = ContextCompat.getColor(this, android.R.color.black)

        // Konversi dp ke pixel untuk strokeWidth
        val strokeWidthPx = resources.displayMetrics.density.toInt() // 1dp dalam pixel
        val noStrokeWidth = 0

        if (isEmailMode) {
            // Email (Terpilih)
            btnEmail.setBackgroundColor(selectedColor)
            btnEmail.setTextColor(defaultWhite)
            btnEmail.strokeWidth = noStrokeWidth // Menggunakan properti strokeWidth MaterialButton

            // Phone Number (Tidak Terpilih)
            btnPhoneNumber.setBackgroundColor(defaultWhite)
            btnPhoneNumber.setTextColor(black)
            btnPhoneNumber.strokeWidth = strokeWidthPx // Menggunakan 1dp
        } else {
            // Email (Tidak Terpilih)
            btnEmail.setBackgroundColor(defaultWhite)
            btnEmail.setTextColor(black)
            btnEmail.strokeWidth = strokeWidthPx // Menggunakan 1dp

            // Phone Number (Terpilih)
            btnPhoneNumber.setBackgroundColor(selectedColor)
            btnPhoneNumber.setTextColor(defaultWhite)
            btnPhoneNumber.strokeWidth = noStrokeWidth
        }
    }
}