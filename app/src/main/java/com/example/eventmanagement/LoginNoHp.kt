package com.example.eventmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventmanagement.databinding.FragmentLoginNoHpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginNoHp : Fragment() {

    private var _binding: FragmentLoginNoHpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    private lateinit var verificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("LoginNoHp", "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w("LoginNoHp", "onVerificationFailed", e)
            (activity as Login).showToast("Verifikasi Gagal: ${e.message}")
        }

        override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("LoginNoHp", "onCodeSent:$vId")
            verificationId = vId
            resendToken = token

            (activity as Login).showToast("Kode verifikasi telah dikirim. Masukkan kode tersebut di kolom Password.")
            binding.passwordInput.hint = "Kode Verifikasi (OTP)"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginNoHpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val phoneNumber = binding.inputFieldPhone.text.toString().trim()
            val code = binding.passwordInput.text.toString().trim()

            if (phoneNumber.isNotEmpty() && phoneNumber.startsWith("+")) {
                if (!::verificationId.isInitialized) {
                    // Langkah 1: Kirim kode
                    startPhoneNumberVerification(phoneNumber)
                } else if (code.isNotEmpty()) {
                    // Langkah 2: Verifikasi kode
                    verifyPhoneNumber(code)
                } else {
                    (activity as Login).showToast("Masukkan kode verifikasi atau tunggu SMS.")
                }
            } else {
                (activity as Login).showToast("Masukkan nomor telepon dengan kode negara (misal: +628...).")
            }
        }

        // ===========================================
        // PERBAIKAN: NAVIGASI KE REGISTER ACTIVITY
        // ===========================================
        binding.createAccountLink.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginNoHp", "signInWithPhoneAuthCredential:success")
                    (activity as Login).showToast("Login/Registrasi Telepon Berhasil!")
                    (activity as Login).navigateToHome()
                } else {
                    Log.w("LoginNoHp", "signInWithPhoneAuthCredential:failure", task.exception)
                    (activity as Login).showToast("Verifikasi Gagal: ${task.exception?.message}")
                }
            }
    }

    private fun verifyPhoneNumber(code: String) {
        if (::verificationId.isInitialized) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneAuthCredential(credential)
        } else {
            (activity as Login).showToast("ID Verifikasi tidak ditemukan. Coba kirim ulang kode.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}