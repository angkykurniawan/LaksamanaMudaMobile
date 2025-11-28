package com.example.eventmanagement // Ganti dengan package Anda

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventmanagement.databinding.FragmentLoginNoHpBinding


class LoginNoHp : Fragment() {

    private var _binding: FragmentLoginNoHpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout untuk Fragment Phone Number
        _binding = FragmentLoginNoHpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Logika View untuk Fragment Phone Number (misalnya listener tombol) diletakkan di sini
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}