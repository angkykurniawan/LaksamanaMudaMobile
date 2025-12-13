package com.example.eventmanagement.event

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.eventmanagement.R

class NavigationFragment : Fragment() { // Sebaiknya diganti namanya menjadi EventFilterFragment

    // FIX: Unresolved reference 'EventViewModel' sekarang resolved dengan placeholder
    private val viewModel: EventViewModel by activityViewModels()

    private lateinit var btnUpcoming: Button
    private lateinit var btnPending: Button
    private lateinit var btnHistory: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Asumsi R.layout.fragment_navigation adalah layout tempat tombol filter berada (misal: layoutFilters)
        // Jika fragment ini digunakan sebagai layout terpisah di dalam EventManagementFragment, ini benar.
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)

        // Inisialisasi Tombol (Asumsi ID btn_upcoming/pending/history ada di layout)
        btnUpcoming = view.findViewById(R.id.btnUpcoming) // FIX: Sinkronisasi ID dengan EventManagementFragment XML
        btnPending = view.findViewById(R.id.btnPending) // FIX: Sinkronisasi ID dengan EventManagementFragment XML
        btnHistory = view.findViewById(R.id.btnHistory) // FIX: Sinkronisasi ID dengan EventManagementFragment XML

        setupListeners()
        observeFilter()

        return view
    }

    private fun setupListeners() {
        // FIX: Unresolved reference 'setFilter' sekarang resolved
        btnUpcoming.setOnClickListener { viewModel.setFilter("Upcoming") }
        btnPending.setOnClickListener { viewModel.setFilter("Pending") }
        btnHistory.setOnClickListener { viewModel.setFilter("History") }
    }

    private fun observeFilter() {
        viewModel.currentFilter.observe(viewLifecycleOwner) { filter ->
            updateButtonStyles(filter)
        }
    }

    private fun updateButtonStyles(activeFilter: String) {
        val context = context ?: return

        // Warna Latar Belakang Default (Abu-abu)
        val defaultBgColor = ContextCompat.getColor(context, android.R.color.darker_gray)
        // Warna Aktif (Orange - sesuai mockup)
        val activeBgColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark)

        // Reset semua gaya
        listOf(btnUpcoming, btnPending, btnHistory).forEach { button ->
            // FIX: Menggunakan drawable background yang benar untuk tombol (misal: bg_status_rounded)
            button.setBackgroundResource(R.drawable.bg_status_rounded)
            // Ganti warna background default menjadi Abu-abu
            button.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.darker_gray)
            button.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        }

        // Terapkan gaya aktif
        when (activeFilter) {
            "Upcoming" -> setButtonStyleActive(btnUpcoming, activeBgColor, context)
            "Pending" -> setButtonStyleActive(btnPending, activeBgColor, context)
            "History" -> setButtonStyleActive(btnHistory, activeBgColor, context)
        }
    }

    private fun setButtonStyleActive(button: Button, color: Int, context: Context) {
        // Terapkan warna aktif (Orange)
        button.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.holo_orange_dark)
        button.setTextColor(ContextCompat.getColor(context, android.R.color.white))
    }
}