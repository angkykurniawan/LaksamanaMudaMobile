package com.example.eventmanagement.bottombar

import android.app.Fragment
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventmanagement.R
import com.example.eventmanagement.bottombar.HomeFragment
import com.example.eventmanagement.bottombar.TeamFragment
import com.example.eventmanagement.bottombar.EventFragment
import com.example.eventmanagement.bottombar.TicketFragment
import com.example.eventmanagement.bottombar.CustomerFragment
import com.example.eventmanagement.databinding.ActivityBottomNavigationViewBinding

class BottomNavigationView : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationViewBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tentukan HomeFragment sebagai tampilan awal saat aplikasi pertama kali dibuka
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // ... (Listener untuk bottomNavigation tetap sama)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_team -> {
                    replaceFragment(TeamFragment())
                    true
                }
                R.id.nav_event -> {
                    replaceFragment(EventFragment())
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
                else -> false
            }
        }
    }

    /**
     * Fungsi helper untuk mengganti Fragment yang ditampilkan di FragmentContainerView.
     * @param fragment Fragment yang akan ditampilkan.
     */
    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) { // Tipe Fragment di sini sekarang adalah androidx.fragment.app.Fragment
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}