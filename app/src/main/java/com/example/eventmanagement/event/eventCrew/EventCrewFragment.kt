package com.example.eventmanagement.event.eventCrew

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventmanagement.R
import com.example.eventmanagement.team.Crew

import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventmanagement.databinding.FragmentEventCrewBinding
import com.google.firebase.database.*

class EventCrewFragment : Fragment(), EventCrewActionListener {

    private var _binding: FragmentEventCrewBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var crewList: ArrayList<EventCrew>
    private lateinit var crewAdapter: EventCrewAdapter
    private var eventId: String? = null
    private val TAG = "EVENT_CREW_FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventCrewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil EVENT_ID dari argument
        eventId = arguments?.getString("EVENT_ID")
        if (eventId == null) {
            Toast.makeText(context, "Error: Event ID tidak ditemukan.", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }

        // Database merujuk ke Crew yang terikat pada Event spesifik ini
        database = FirebaseDatabase.getInstance().getReference("crews").child(eventId!!)
        crewList = arrayListOf()

        setupViews()
        setupRecyclerView()
        fetchCrewData() // <-- FUNGSI BACKEND UTAMA (READ)
    }

    private fun setupViews() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        // Navigasi ke Fragment Add Crew
        binding.cardAddCrew.setOnClickListener { navigateToAddCrewFragment() }
    }

    private fun setupRecyclerView() {
        binding.rvCrewList.layoutManager = LinearLayoutManager(context)
        crewAdapter = EventCrewAdapter(crewList, this)
        binding.rvCrewList.adapter = crewAdapter
    }

    private fun fetchCrewData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentBinding = _binding ?: return
                crewList.clear()

                if (snapshot.exists()) {
                    for (crewSnapshot in snapshot.children) {
                        // Menggunakan model EventCrew.kt
                        val crew = crewSnapshot.getValue(EventCrew::class.java)
                        if (crew != null) {
                            crewList.add(crew)
                        }
                    }
                }
                currentBinding.tvTotalCrewCount.text = crewList.size.toString()
                crewAdapter.notifyDataSetChanged()

                // Tampilkan pesan jika daftar kosong
                currentBinding.rvCrewList.visibility = if (crewList.isEmpty()) View.GONE else View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Gagal memuat data crew: ${error.message}")
                if (context != null) {
                    Toast.makeText(context, "Gagal memuat data.", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    // FUNGSI BACKEND UTAMA (DELETE)
    override fun onDeleteClick(crew: EventCrew) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Crew")
            .setMessage("Apakah Anda yakin ingin menghapus '${crew.name}' dari tim Event ini?")
            .setPositiveButton("Hapus") { _, _ -> deleteCrew(crew.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteCrew(crewId: String?) {
        if (crewId == null) return
        database.child(crewId).removeValue()
            .addOnSuccessListener { Toast.makeText(context, "Crew berhasil dihapus dari Event!", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(context, "Gagal menghapus crew.", Toast.LENGTH_SHORT).show() }
    }

    private fun navigateToAddCrewFragment() {
        val fragment = AddCrewFragment()
        val args = Bundle().apply { putString("EVENT_ID", eventId) }
        fragment.arguments = args

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}