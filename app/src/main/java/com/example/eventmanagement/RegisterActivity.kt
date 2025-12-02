package com.example.eventmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventmanagement.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import androidx.appcompat.app.AlertDialog

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan nama binding sesuai dengan nama file XML: activity_register.xml -> ActivityRegisterBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        setupListeners()
    }

    private fun setupListeners() {
        // Tombol Kembali
        binding.backButton.setOnClickListener {
            finish() // Kembali ke LoginActivity
        }

        // Tombol Register (di XML bernama 'Update')
        binding.registerButton.setOnClickListener {
            performRegistration() // Panggil fungsi pendaftaran
        }
    }

    private fun performRegistration() {
        val username = binding.inputUsername.text.toString().trim()
        val email = binding.inputEmail.text.toString().trim()
        val password = binding.inputPassword.text.toString()
        val confirmPassword = binding.inputConfirmPassword.text.toString()

        // 1. Validasi Input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password dan Konfirmasi Password tidak cocok.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password minimal 6 karakter.", Toast.LENGTH_SHORT).show()
            return
        }

        // --- FEEDBACK INSTAN: Tampilkan pesan dan nonaktifkan tombol ---
        Toast.makeText(this, "Memproses pendaftaran...", Toast.LENGTH_SHORT).show()
        binding.registerButton.isEnabled = false // Menonaktifkan tombol

        // 2. Registrasi Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                // --- PENTING: Aktifkan kembali tombol di sini, terlepas dari hasil ---
                binding.registerButton.isEnabled = true

                if (task.isSuccessful) {
                    // Jika Auth sukses, lanjutkan menyimpan data ke Firestore
                    Log.d("Register", "Registrasi Auth berhasil.")
                    val user = auth.currentUser

                    if (user != null) {
                        updateUserProfile(user.uid, username, email)
                    }

                } else {
                    // Jika Auth gagal (misal: email sudah terdaftar), tampilkan dialog GAGAL
                    val errorMessage = task.exception?.message ?: "Terjadi kesalahan yang tidak diketahui."
                    Log.w("Register", "Registrasi gagal.", task.exception)
                    showFailureDialog(errorMessage)
                }
            }
    }

    // Fungsi untuk update display name di Auth dan simpan data di Firestore
    private fun updateUserProfile(userId: String, username: String, email: String) {
        val user = auth.currentUser
        val db = Firebase.firestore

        // 1. Update Display Name di Firebase Auth (Opsional, tapi praktik baik)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { profileTask ->
                if (profileTask.isSuccessful) {
                    Log.d("Register", "Username berhasil diatur.")
                }
            }

        // 2. Simpan data tambahan di Firestore (disimpan di collection 'users')
        val userProfile = hashMapOf(
            "username" to username,
            "email" to email,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("users").document(userId).set(userProfile)
            .addOnSuccessListener {
                // TAMPILKAN DIALOG SUKSES (KARENA AUTH DAN FIRESTORE SUKSES)
                Log.d("Register", "Data user berhasil disimpan di Firestore.")

                // SIGNOUT dan tampilkan dialog sukses
                auth.signOut()
                showSuccessDialog()
            }
            .addOnFailureListener { e ->
                // TAMPILKAN DIALOG GAGAL (KARENA FIRESTORE GAGAL)
                Log.e("Register", "Gagal menyimpan data user ke Firestore: $e")

                // Akun Auth sudah dibuat, jadi tetap perlu logout agar navigasi berjalan lancar.
                auth.signOut()

                // Tampilkan pesan gagal penyimpanan Firestore
                showFailureDialog("Registrasi Auth berhasil, namun penyimpanan data profil gagal. Silakan coba login.")
            }
    }

    /**
     * Menampilkan dialog sukses dan mengalihkan ke halaman Login.
     */
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Registrasi Berhasil!")
            .setMessage("Akun Anda telah berhasil dibuat. Silakan login menggunakan email dan password yang baru.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                navigateToLogin() // Alihkan ke LoginActivity
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Menampilkan dialog gagal dan meminta pengguna mencoba lagi.
     */
    private fun showFailureDialog(errorMessage: String) {
        AlertDialog.Builder(this)
            .setTitle("Registrasi Gagal")
            .setMessage("Gagal membuat akun. Pesan Error: ${errorMessage}\n\nSilakan coba lagi.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Jika kegagalan adalah kegagalan penyimpanan Firestore setelah Auth sukses, kita alihkan ke Login
                if (errorMessage.contains("Registrasi Auth berhasil")) {
                    navigateToLogin()
                }
            }
            .setCancelable(true)
            .show()
    }

    /**
     * Mengalihkan pengguna kembali ke LoginActivity.
     */
    private fun navigateToLogin() {
        // Class Login yang dimaksud adalah class Login Activity container Anda
        val intent = Intent(this, Login::class.java)
        // Bersihkan stack agar user tidak bisa kembali ke halaman Register
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}