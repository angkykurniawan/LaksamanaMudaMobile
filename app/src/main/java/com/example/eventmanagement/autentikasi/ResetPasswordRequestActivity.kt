package com.example.eventmanagement.autentikasi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventmanagement.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class ResetPasswordRequestActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnRecover: Button
    private val database = FirebaseDatabase.getInstance().getReference("anggota")
    private val TAG = "RESET_PASSWORD_REQUEST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password_request)

        etEmail = findViewById(R.id.inputFieldEmail)
        btnRecover = findViewById(R.id.recoverPasswordButton)

        btnRecover.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                etEmail.error = "Email harus diisi."
            } else {
                requestOtp(email)
            }
        }
    }

    private fun requestOtp(email: String) {
        btnRecover.isEnabled = false
        Toast.makeText(this, "⏳ Memeriksa email...", Toast.LENGTH_SHORT).show()

        // 1. Cari Anggota berdasarkan email
        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    btnRecover.isEnabled = true

                    if (snapshot.exists()) {
                        val anggotaSnapshot = snapshot.children.first()
                        val anggotaId = anggotaSnapshot.key

                        if (anggotaId != null) {
                            // *** START: LOGIKA CLIENT-SIDE OTP GENERATION ***

                            // 2. Generate OTP 6 digit di KLIEN
                            val otp = String.format("%06d", Random.nextInt(1000000))

                            // 3. Simpan OTP ke NODE ANGGOTA/ID/otp_temp (sesuai permintaan)
                            database.child(anggotaId).child("otp_temp").setValue(otp)
                                .addOnSuccessListener {
                                    // Peringatan Keamanan: Karena tidak ada email yang dikirim,
                                    // kita harus menampilkan kode OTP di log atau toast untuk debugging.
                                    Log.d(TAG, "OTP $otp disimpan di database untuk ID $anggotaId.")
                                    Toast.makeText(this@ResetPasswordRequestActivity,
                                        "Simulasi: OTP Anda adalah $otp. Masukkan kode ini.",
                                        Toast.LENGTH_LONG).show()

                                    // 4. Navigasi ke halaman verifikasi OTP
                                    val intent = Intent(this@ResetPasswordRequestActivity, OtpVerificationActivity::class.java)
                                    intent.putExtra("ANGGOTA_ID", anggotaId)
                                    intent.putExtra("EMAIL", email)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(this@ResetPasswordRequestActivity,
                                        "Gagal menyimpan OTP di Firebase: ${error.message}",
                                        Toast.LENGTH_LONG).show()
                                    Log.e(TAG, "Gagal menyimpan OTP: ${error.message}")
                                }

                            // *** END: LOGIKA CLIENT-SIDE OTP GENERATION ***
                        } else {
                            Toast.makeText(this@ResetPasswordRequestActivity, "ID anggota tidak valid.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Email tidak ditemukan
                        Toast.makeText(this@ResetPasswordRequestActivity, "Email tidak terdaftar.", Toast.LENGTH_LONG).show()
                        Log.w(TAG, "Email $email tidak ditemukan.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    btnRecover.isEnabled = true
                    Toast.makeText(this@ResetPasswordRequestActivity, "Error koneksi Firebase: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}