package com.example.eventmanagement.autentikasi // Ganti dengan package Anda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.eventmanagement.bottombar.BottomNavigationView
import com.example.eventmanagement.databinding.FragmentLoginEmailBinding // Pastikan nama Binding Class Anda benar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest

class LoginEmail : Fragment() {

    private var _binding: FragmentLoginEmailBinding? = null
    private val binding get() = _binding!!
    private val database = FirebaseDatabase.getInstance().getReference("anggota")
    private val TAG = "LOGIN_EMAIL_FIREBASE"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Asumsi ID tombol login Anda adalah 'btnLogin' atau 'loginButton'
        // Berdasarkan XML terakhir Anda, ID-nya adalah 'btnLogin'
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        // Ambil input dari ID yang sudah disinkronkan di XML: etEmail, etPassword
        val email = binding.etEmail.text.toString().trim()
        val passwordInput = binding.etPassword.text.toString()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email wajib diisi"
            return
        }
        if (passwordInput.isEmpty()) {
            binding.etPassword.error = "Password wajib diisi"
            return
        }

        binding.btnLogin.isEnabled = false
        Toast.makeText(context, "⏳ Mencari data anggota...", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Mencoba login dengan Email: $email")

        // Mencari data di Firebase berdasarkan email
        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.btnLogin.isEnabled = true

                    if (snapshot.exists()) {
                        // Email ditemukan
                        for (anggotaSnapshot in snapshot.children) {
                            val anggota = anggotaSnapshot.getValue(Anggota::class.java)
                            if (anggota != null) {

                                val storedHashedPassword = anggota.password
                                val inputHashedPassword = hashPassword(passwordInput)

                                // Membandingkan password yang di-hash
                                if (storedHashedPassword == inputHashedPassword) {
                                    // *** NAVIGASI KE HOME NAVIGATOR (SUKSES) ***
                                    Toast.makeText(context, "✅ Login Berhasil! Selamat datang, ${anggota.name}.", Toast.LENGTH_LONG).show()
                                    Log.i(TAG, "Login berhasil. Navigasi ke HomeNavActivity.")

                                    val intent = Intent(requireActivity(), BottomNavigationView::class.java)
                                    // Membersihkan back stack agar pengguna tidak bisa kembali ke halaman Login/Register
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    requireActivity().finish() // Tutup Login Activity container

                                } else {
                                    // Login GAGAL: Password salah
                                    Toast.makeText(context, "❌ Login Gagal: Password tidak cocok.", Toast.LENGTH_LONG).show()
                                    Log.w(TAG, "Password salah untuk anggota: ${anggota.email}")
                                }
                                return
                            }
                        }
                    } else {
                        // Login GAGAL: Email tidak ditemukan
                        Toast.makeText(context, "❌ Login Gagal: Email tidak terdaftar.", Toast.LENGTH_LONG).show()
                        Log.w(TAG, "Email $email tidak ditemukan.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.btnLogin.isEnabled = true
                    // GAGAL: Masalah koneksi atau permission Firebase
                    Toast.makeText(context, "❌ Error Firebase: ${error.message}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Query Firebase dibatalkan: ${error.details}")
                }
            })
    }

    // Fungsi utilitas untuk hashing password SHA-256
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}