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
    private lateinit var database: DatabaseReference
    private lateinit var eventList: ArrayList<Event>
    private lateinit var eventAdapter: EventAdapter
    private val TAG = "EVENT_FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Asumsi layout XML Fragment Anda adalah fragment_event_management.xml
        _binding = FragmentEventManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("events") // Node baru untuk data events
        eventList = arrayListOf()

        setupRecyclerView()
        fetchEventData()

        binding.cardAddEvent.setOnClickListener {
            // Navigasi ke Activity untuk menambah event
            startActivity(Intent(requireContext(), AddEventActivity::class.java))
        }

        // TODO: Tambahkan listener untuk filter Upcoming/Pending/History
    }

    private fun setupRecyclerView() {
        binding.rvEventList.layoutManager = LinearLayoutManager(context)

        eventAdapter = EventAdapter(eventList,
            onActionClick = { event -> showEventActionDialog(event) },
            onInfoClick = { event -> showEventDetailDialog(event) }
        )
        binding.rvEventList.adapter = eventAdapter
    }

    // Dialog Detail Event (sesuai mockup)
    private fun showEventDetailDialog(event: Event) {
        // Implementasi sederhana, bisa dipercantik menggunakan custom dialog
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

    // Dialog Aksi Event (Edit/Delete)
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

    private fun fetchEventData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                var eventCount = 0

                if (snapshot.exists()) {
                    for (eventSnapshot in snapshot.children) {
                        val event = eventSnapshot.getValue(Event::class.java)
                        if (event != null) {
                            eventList.add(event)
                            eventCount++
                        }
                    }
                }

                binding.tvTotalEventCount.text = eventCount.toString() // Asumsi ID ada di XML
                eventAdapter.notifyDataSetChanged()
                Log.d(TAG, "Data event berhasil dimuat. Total: $eventCount")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Gagal memuat data event: ${error.message}")
                Toast.makeText(context, "Gagal memuat data event: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}