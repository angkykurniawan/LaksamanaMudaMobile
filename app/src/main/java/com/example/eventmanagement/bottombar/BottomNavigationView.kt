package com.example.eventmanagement.bottombar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.eventmanagement.R
import com.example.eventmanagement.databinding.ActivityBottomNavigationViewBinding
import com.example.eventmanagement.event.EventManagementFragment

// Asumsi: Semua Fragment ini (HomeFragment, TeamFragment, dll.) sudah didefinisikan dengan benar
// dalam paket com.example.eventmanagement.bottombar
class BottomNavigationView : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan nama file layout yang benar adalah activity_bottom_navigation_view.xml
        binding = ActivityBottomNavigationViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener untuk insets sistem (UI/StatusBar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tentukan HomeFragment sebagai tampilan awal saat aplikasi pertama kali dibuka
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())

            // --- PERBAIKAN UTAMA DI SINI ---
            // Menggunakan setSelectedItemId untuk memaksa BottomNavigationView memilih item placeholder Home.
            // Ini akan menonaktifkan sorotan default pada item pertama (Team).
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home)
        }

        // 1. LISTENER UNTUK FLOATING ACTION BUTTON (FAB)
        binding.fabHome.setOnClickListener {
            replaceFragment(HomeFragment())
            // Pastikan item placeholder/home di BottomNav ditandai seolah-olah terpilih
            binding.bottomNavigation.menu.findItem(R.id.nav_home)?.isChecked = true
        }

        // 2. LISTENER UNTUK ITEM BOTTOM NAVIGATION (4 item + 1 Placeholder)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_team -> {
                    replaceFragment(TeamFragment())
                    true
                }
                R.id.nav_event -> {
                    replaceFragment(EventManagementFragment())
                    true
                }
                R.id.nav_ticket -> {
                    replaceFragment(TicketFragment())
                    true
                }
                R.id.nav_customer -> {
                    replaceFragment(CustomerFragment())
                    true
                }
                R.id.nav_home -> {
                    // Ketika item placeholder Home di BottomNav diklik,
                    // kita navigasi ke HomeFragment (melalui FAB)
                    binding.fabHome.performClick()
                    false // Return false agar efek klik tidak berlebihan di BottomNav
                }
                else -> false
            }
        }
    }

    /**
     * Fungsi helper untuk mengganti Fragment yang ditampilkan.
     * @param fragment Fragment yang akan ditampilkan (harus dari androidx.fragment.app.Fragment).
     */
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}