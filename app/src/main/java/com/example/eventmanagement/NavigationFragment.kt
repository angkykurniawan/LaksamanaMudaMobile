package com.example.eventmanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventmanagement.viewmodels.EventViewModel
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.eventmanagement.R


class NavigationFragment : Fragment() {
    private val viewModel: EventViewModel by activityViewModels()

    private lateinit var btnUpcoming: Button
    private lateinit var btnPending: Button
    private lateinit var btnHistory: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)

        btnUpcoming = view.findViewById(R.id.btn_upcoming)
        btnPending = view.findViewById(R.id.btn_pending)
        btnHistory = view.findViewById(R.id.btn_history)

        setupListeners()
        observeFilter()

        return view
    }

    private fun setupListeners() {
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

        // Reset semua gaya
        listOf(btnUpcoming, btnPending, btnHistory).forEach { button ->
            button.setBackgroundResource(R.drawable.home_white)
            button.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        // Terapkan gaya aktif
        when (activeFilter) {
            "Upcoming" -> setButtonStyleActive(btnUpcoming, context)
            "Pending" -> setButtonStyleActive(btnPending, context)
            "History" -> setButtonStyleActive(btnHistory, context)
        }
    }

    private fun setButtonStyleActive(button: Button, context: android.content.Context) {
        button.setBackgroundResource(R.drawable.home_white)
        button.setTextColor(ContextCompat.getColor(context, android.R.color.white))
    }
}