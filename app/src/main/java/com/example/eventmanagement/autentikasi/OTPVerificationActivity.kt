package com.example.eventmanagement.autentikasi


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventmanagement.R
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.example.eventmanagement.autentikasi.ResetPasswordActivity
// Class yang merepresentasikan halaman verifikasi kode OTP

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var etOtp: EditText
    private lateinit var btnVerify: Button
    private lateinit var tvEmail: TextView
    private var anggotaId: String? = null
    private var email: String? = null
    private val TAG = "OTP_VERIFICATION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        anggotaId = intent.getStringExtra("ANGGOTA_ID")
        email = intent.getStringExtra("EMAIL")

        etOtp = findViewById(R.id.inputFieldOtp)
        btnVerify = findViewById(R.id.verifyOtpButton)
        tvEmail = findViewById(R.id.verificationEmailText)

        tvEmail.text = "Kode OTP dikirim ke: $email"

        if (anggotaId == null) {
            Toast.makeText(this, "Error: Sesi verifikasi tidak valid.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        btnVerify.setOnClickListener {
            val inputOtp = etOtp.text.toString().trim()
            if (inputOtp.length == 6 && anggotaId != null) {
                verifyOtp(anggotaId!!, inputOtp)
            } else {
                Toast.makeText(this, "Kode OTP harus 6 digit.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyOtp(id: String, inputOtp: String) {
        btnVerify.isEnabled = false

        // Ambil OTP yang tersimpan dari NODE ANGGOTA/ID/otp_temp
        FirebaseDatabase.getInstance().getReference("anggota").child(id).child("otp_temp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    btnVerify.isEnabled = true
                    val storedOtp = snapshot.getValue(String::class.java)
                    Log.d(TAG, "Membaca OTP tersimpan: $storedOtp")

                    if (storedOtp != null && storedOtp == inputOtp) {
                        // Verifikasi SUKSES

                        // 2. Hapus field OTP dari node anggota (PENTING!)
                        snapshot.ref.removeValue()

                        // 3. Navigasi ke halaman Reset Password
                        Toast.makeText(this@OtpVerificationActivity, "Verifikasi sukses! Silakan ganti password baru.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@OtpVerificationActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("ANGGOTA_ID", id)
                        startActivity(intent)
                        finish()

                    } else {
                        // Verifikasi GAGAL
                        Toast.makeText(this@OtpVerificationActivity, "Kode OTP salah atau kedaluwarsa.", Toast.LENGTH_LONG).show()
                        Log.w(TAG, "Verifikasi gagal. Input: $inputOtp, Tersimpan: $storedOtp")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    btnVerify.isEnabled = true
                    Toast.makeText(this@OtpVerificationActivity, "Error mengambil data OTP: ${error.message}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Error Firebase: ${error.message}")
                }
            })
    }
}