package com.example.eventmanagement.event

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventmanagement.HeaderFragment
import com.example.eventmanagement.NavigationFragment
import com.example.eventmanagement.R
import com.example.eventmanagement.StatsActionsFragment

class EventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- LOGIKA UTAMA DITAMBAHKAN DI SINI ---
        if (savedInstanceState == null) {
            loadAllFragments()
        }
    }

    private fun loadAllFragments() {
        val fragmentManager = supportFragmentManager

        fragmentManager.beginTransaction().apply {
            // Memuat keempat Fragment ke dalam container masing-masing
            add(R.id.fragment_container_header, HeaderFragment())
            add(R.id.fragment_container_navigation, NavigationFragment())
            add(R.id.fragment_container_stats_actions, StatsActionsFragment())
            add(R.id.fragment_container_event_list, EventListFragment())

            commit()
        }
    }
}