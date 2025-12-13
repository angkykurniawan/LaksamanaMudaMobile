package com.example.eventmanagement.bottombar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.eventmanagement.R
import com.example.eventmanagement.customer.CustomerEngagementFragment
import com.example.eventmanagement.event.EventManagementFragment
import com.example.eventmanagement.team.TeamFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BottomNavigationView : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabHome: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation_view)

        bottomNav = findViewById(R.id.bottom_navigation)
        fabHome = findViewById(R.id.fab_home)

        // Set fragment default saat aplikasi pertama kali dibuka
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            bottomNav.selectedItemId = R.id.nav_home
        }

        // Listener FAB Home
        fabHome.setOnClickListener {
            replaceFragment(HomeFragment())
            bottomNav.menu.findItem(R.id.nav_home).isChecked = true
        }

        // Listener BottomNavigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> fabHome.performClick()
                R.id.nav_team -> replaceFragment(TeamFragment())
                R.id.nav_event -> replaceFragment(EventManagementFragment())
                R.id.nav_ticket -> replaceFragment(TicketFragment())
                R.id.nav_customer -> replaceFragment(CustomerEngagementFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
