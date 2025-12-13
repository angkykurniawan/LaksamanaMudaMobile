package com.example.eventmanagement.event

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventmanagement.HeaderFragment
import com.example.eventmanagement.event.NavigationFragment
import com.example.eventmanagement.R
import com.example.eventmanagement.adapters.EventAdapter
import com.example.eventmanagement.databinding.FragmentEventManagementBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.ktor.client.content.LocalFileContent

class EventManagementFragment : Fragment() {

    private var _binding: FragmentEventManagementBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi ViewModel yang sama digunakan oleh NavigationFragment
    private val eventViewModel: EventViewModel by activityViewModels()

    private lateinit var database: DatabaseReference
    private var allEventList: ArrayList<Event> = arrayListOf() // Menyimpan semua data dari Firebase
    private lateinit var filteredEventList: ArrayList<Event> // Daftar yang difilter untuk RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private val TAG = "EVENT_FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("events")
        filteredEventList = arrayListOf() // Inisialisasi daftar filter

        setupRecyclerView()
        fetchEventData()

        // Mulai mengamati perubahan filter dari ViewModel
        observeFilterChanges()

        binding.cardAddEvent.setOnClickListener {
            startActivity(Intent(requireContext(), AddEventActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        binding.rvEventList.layoutManager = LinearLayoutManager(context)

        eventAdapter = EventAdapter(filteredEventList,
            onActionClick = { event -> showEventActionDialog(event) },
            onInfoClick = { event -> showEventDetailDialog(event) }
        )
        binding.rvEventList.adapter = eventAdapter
    }

    private fun observeFilterChanges() {
        eventViewModel.currentFilter.observe(viewLifecycleOwner) { filter ->
            Log.d(TAG, "Filter aktif diubah menjadi: $filter")
            // Setiap kali filter berubah, kita panggil fungsi filtering
            applyFilterToEvents(filter)
        }
    }

    private fun applyFilterToEvents(filter: String) {
        filteredEventList.clear()

        // Logika Filtering
        if (filter.equals("All", ignoreCase = true)) {
            // Jika ada filter "All", tampilkan semua event
            filteredEventList.addAll(allEventList)
        } else {
            // Terapkan filter berdasarkan status
            val filtered = allEventList.filter { event ->
                event.status?.equals(filter, ignoreCase = true) == true
            }
            filteredEventList.addAll(filtered)
        }

        // Perbarui tampilan RecyclerView
        binding.tvTotalEventCount.text = filteredEventList.size.toString()
        eventAdapter.notifyDataSetChanged()

        if (filteredEventList.isEmpty()) {
            Toast.makeText(context, "Tidak ada event dengan status '$filter'.", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi ini sekarang hanya berfungsi untuk memuat data MENTAH dari Firebase
    private fun fetchEventData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allEventList.clear() // Bersihkan daftar mentah

                if (snapshot.exists()) {
                    for (eventSnapshot in snapshot.children) {
                        val event = eventSnapshot.getValue(Event::class.java)
                        if (event != null) {
                            allEventList.add(event)
                        }
                    }
                }

                Log.d(TAG, "Data mentah dimuat. Total: ${allEventList.size}")

                // Setelah data mentah dimuat, terapkan filter yang sedang aktif
                val activeFilter = eventViewModel.currentFilter.value ?: "Upcoming" // Default filter
                applyFilterToEvents(activeFilter)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Gagal memuat data event: ${error.message}")
                Toast.makeText(context, "Gagal memuat data event: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // ... (showEventDetailDialog, showEventActionDialog, navigateToEditEvent, confirmDeleteEvent, deleteEvent tetap sama)

    private fun showEventDetailDialog(event: Event) {
        // Implementasi dialog detail
        val message = "Event: ${event.name}\n" +
                "Status: ${event.status}\n" +
                "Description: ${event.description}\n" +
                "Price: ${event.priceRange}\n" +
                "Date: ${event.date}"

        AlertDialog.Builder(requireContext())
            .setTitle("Detail Event")
            .setMessage(message)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showEventActionDialog(event: Event) {
        val options = arrayOf("Edit Event", "Delete Event")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Aksi")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> navigateToEditEvent(event.id)
                    1 -> confirmDeleteEvent(event)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToEditEvent(eventId: String?) {
        if (eventId != null) {
            val intent = Intent(requireContext(), EditEventActivity::class.java)
            intent.putExtra("EVENT_ID", eventId)
            startActivity(intent)
        } else {
            Toast.makeText(context, "ID Event tidak valid untuk diedit.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDeleteEvent(event: Event) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Event")
            .setMessage("Apakah Anda yakin ingin menghapus Event ${event.name}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteEvent(event.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteEvent(eventId: String?) {
        if (eventId == null) return

        database.child(eventId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Event berhasil dihapus!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal menghapus event.", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}