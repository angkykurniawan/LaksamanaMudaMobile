package com.example.eventmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventmanagement.databinding.FragmentLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginEmail : Fragment() {

    private var _binding: FragmentLoginEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listener untuk tombol LOGIN
        binding.loginButton.setOnClickListener {
            val email = binding.inputFieldEmail.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInEmail(email, password)
            } else {
                (activity as Login).showToast("Email dan Password tidak boleh kosong.")
            }
        }

        // ===========================================
        // PERBAIKAN: NAVIGASI KE REGISTER ACTIVITY
        // ===========================================
        binding.createAccountLink.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Listener untuk Forgot Password
        binding.forgotPasswordLink.setOnClickListener {
            val email = binding.inputFieldEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                sendPasswordReset(email)
            } else {
                (activity as Login).showToast("Masukkan email Anda untuk reset password.")
            }
        }
    }

    private fun signInEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginEmail", "signInWithEmail:success")
                    (activity as Login).showToast("Login Berhasil!")
                    (activity as Login).navigateToHome()
                } else {
                    Log.w("LoginEmail", "signInWithEmail:failure", task.exception)
                    (activity as Login).showToast("Login Gagal: ${task.exception?.message}")
                }
            }
    }

    private fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    (activity as Login).showToast("Link reset password telah dikirim ke $email")
                } else {
                    (activity as Login).showToast("Gagal mengirim link reset: ${task.exception?.message}")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}