package com.example.eventmanagement.autentikasi // Ganti dengan package Anda

import android.os.Bundle
// Pastikan Anda mengimpor MaterialButton jika Anda menggunakannya
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.eventmanagement.R
import com.example.eventmanagement.databinding.ActivityLoginBinding // Ganti ke nama binding Activity container Anda (sebelumnya ActivityLoginBinding)

// HAPUS BARIS YANG ERROR: private var Button.strokeWidth: Int
// Jika binding Anda ActivityLoginBinding, pastikan Activity Anda adalah Login, bukan LoginContainerActivity

class Login: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val fragmentEmail = LoginEmail()
    private val fragmentNoHp = LoginNoHp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val btnEmail = binding.btnEmail as MaterialButton
        val btnPhoneNumber = binding.btnPhoneNumber as MaterialButton

        val selectedColor = ContextCompat.getColor(this, R.color.Oren)
        val defaultWhite = ContextCompat.getColor(this, android.R.color.white)
        val black = ContextCompat.getColor(this, android.R.color.black)

        val strokeWidthPx = resources.displayMetrics.density.toInt()
        val noStrokeWidth = 0

        if (isEmailMode) {
            // Email (Terpilih)
            btnEmail.setBackgroundColor(selectedColor)
            btnEmail.setTextColor(defaultWhite)
            btnEmail.strokeWidth = noStrokeWidth

            // Phone Number (Tidak Terpilih)
            btnPhoneNumber.setBackgroundColor(defaultWhite)
            btnPhoneNumber.setTextColor(black)
            btnPhoneNumber.strokeWidth = strokeWidthPx
        } else {
            // Email (Tidak Terpilih)
            btnEmail.setBackgroundColor(defaultWhite)
            btnEmail.setTextColor(black)
            btnEmail.strokeWidth = strokeWidthPx

            // Phone Number (Terpilih)
            btnPhoneNumber.setBackgroundColor(selectedColor)
            btnPhoneNumber.setTextColor(defaultWhite)
            btnPhoneNumber.strokeWidth = noStrokeWidth
        }
    }
}