package com.example.eventmanagement.team

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eventmanagement.R
import com.example.eventmanagement.databinding.FragmentTeamBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.recyclerview.widget.LinearLayoutManager // FIX: Unresolved reference 'LinearLayoutManager'

class TeamFragment : Fragment() {

    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var crewList: ArrayList<Crew> // Crew di-resolve
    private lateinit var crewAdapter: CrewAdapter // CrewAdapter di-resolve
    private val TAG = "TEAM_FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("crew")
        crewList = arrayListOf()

        setupRecyclerView()
        fetchCrewData()

        // Listener klik pada card Add Crew
        binding.cardAddCrew.setOnClickListener {
            // AddCrewActivity di-resolve
            startActivity(Intent(requireContext(), AddCrewActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        binding.rvCrewList.layoutManager = LinearLayoutManager(context)

        // Inisialisasi adapter dengan listener untuk Edit dan Delete
        crewAdapter = CrewAdapter(crewList,
            onEditClick = { crew -> navigateToEditCrew(crew.id) },
            onDeleteClick = { crew -> confirmDeleteCrew(crew) }
        )
        binding.rvCrewList.adapter = crewAdapter
    }

    private fun navigateToEditCrew(crewId: String?) {
        if (crewId != null) {
            // EditCrewActivity di-resolve
            val intent = Intent(requireContext(), EditCrewActivity::class.java)
            intent.putExtra("CREW_ID", crewId)
            startActivity(intent)
        } else {
            Toast.makeText(context, "ID Kru tidak valid untuk diedit.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDeleteCrew(crew: Crew) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Kru")
            .setMessage("Apakah Anda yakin ingin menghapus Kru ${crew.name}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteCrew(crew.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteCrew(crewId: String?) {
        if (crewId == null) return

        database.child(crewId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Kru berhasil dihapus!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal menghapus kru.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchCrewData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                crewList.clear()
                var crewCount = 0

                if (snapshot.exists()) {
                    for (crewSnapshot in snapshot.children) {
                        val crew = crewSnapshot.getValue(Crew::class.java)
                        if (crew != null) {
                            crewList.add(crew)
                            crewCount++
                        }
                    }
                }

                binding.tvTotalCrewCount.text = crewCount.toString()
                crewAdapter.notifyDataSetChanged()
                Log.d(TAG, "Data kru berhasil dimuat. Total: $crewCount")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Gagal memuat data kru: ${error.message}")
                Toast.makeText(context, "Gagal memuat data kru: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}