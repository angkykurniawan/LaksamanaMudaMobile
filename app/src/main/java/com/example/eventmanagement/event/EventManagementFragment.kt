package com.example.eventmanagement.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventmanagement.HeaderFragment
import com.example.eventmanagement.NavigationFragment
import com.example.eventmanagement.R
import com.example.eventmanagement.StatsActionsFragment

class EventManagementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout yang berfungsi sebagai container untuk 4 sub-Fragment.
        // Asumsi: R.layout.fragment_event_management memiliki 4 FragmentContainerView.
        val view = inflater.inflate(R.layout.fragment_event_management, container, false)

        // Memuat 4 sub-Fragment hanya saat Fragment dibuat pertama kali
        if (savedInstanceState == null) {
            loadAllSubFragments()
        }

        return view
    }

    /**
     * Memuat semua 4 sub-Fragment ke dalam container menggunakan childFragmentManager.
     */
    private fun loadAllSubFragments() {
        // Penting: Gunakan childFragmentManager karena ini adalah Fragment di dalam Fragment.
        val fragmentManager = childFragmentManager

        fragmentManager.beginTransaction().apply {
            // Fragment 1: Header (Event Management & SearchBar)
            add(R.id.fragment_container_header, HeaderFragment())

            // Fragment 2: Navigasi Kategori (Upcoming, Pending, History)
            add(R.id.fragment_container_navigation, NavigationFragment())

            // Fragment 3: Statistik dan Aksi (Total Event & Add Event)
            add(R.id.fragment_container_stats_actions, StatsActionsFragment())

            // Fragment 4: Daftar Event (RecyclerView)
            add(R.id.fragment_container_event_list, EventListFragment())

            commit()
        }
    }

    // Menghapus newInstance dan kode parameter yang tidak terpakai.
}