package com.example.eventmanagement.event

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eventmanagement.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditEventFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var spStatus: Spinner
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var etPoster: EditText
    private lateinit var etDate: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnBack: ImageButton

    private val eventStatuses = arrayOf("Upcoming", "Pending", "History")

    private lateinit var database: DatabaseReference
    private var eventId: String? = null
    private val TAG = "EDIT_EVENT_FRAGMENT"

    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_edit_event, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("events")

        eventId = arguments?.getString("EVENT_ID")

        if (eventId.isNullOrEmpty()) {
            Toast.makeText(context, "Error: ID Event tidak ditemukan.", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }

        initViews(view)
        loadEventData(eventId!!)
        setupListeners()
    }

    private fun initViews(view: View) {

        btnBack = view.findViewById(R.id.btn_back_edit)
        etName = view.findViewById(R.id.et_name)
        spStatus = view.findViewById(R.id.sp_status)
        etDescription = view.findViewById(R.id.et_description)
        etPrice = view.findViewById(R.id.et_price)
        etPoster = view.findViewById(R.id.et_poster)
        etDate = view.findViewById(R.id.et_date)

        btnSave = view.findViewById(R.id.btn_save_event)
        btnCancel = view.findViewById(R.id.btn_cancel)

        etDate.keyListener = null

        // --- Setup Spinner ---
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            eventStatuses
        )
        spStatus.adapter = adapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }

        etDate.setOnClickListener { showDatePickerDialog(etDate) }
        btnSave.setOnClickListener { handleUpdateEvent() }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            editText.setText(sdf.format(calendar.time))
        }

        DatePickerDialog(requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadEventData(id: String) {
        database.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val event = snapshot.getValue(Event::class.java)
                if (event != null) {
                    etName.setText(event.name)

                    event.status?.let { status ->
                        val position = eventStatuses.indexOf(status)
                        if (position != -1) {
                            spStatus.setSelection(position)
                        }
                    }

                    etDescription.setText(event.description)
                    etPrice.setText(event.priceRange)
                    etPoster.setText(event.posterUrl)
                    etDate.setText(event.date)
                } else {
                    Toast.makeText(context, "Data Event tidak ditemukan.", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleUpdateEvent() {
        val name = etName.text.toString().trim()
        val status = spStatus.selectedItem.toString()
        val description = etDescription.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val poster = etPoster.text.toString().trim()
        val date = etDate.text.toString().trim()

        // --- Validasi Input ---
        if (name.isEmpty()) { etName.error = "Nama wajib diisi"; return }
        if (description.isEmpty()) { etDescription.error = "Deskripsi wajib diisi"; return }
        if (price.isEmpty()) { etPrice.error = "Harga wajib diisi"; return }
        if (poster.isEmpty()) { etPoster.error = "Poster URL/Nama wajib diisi"; return }
        if (date.isEmpty()) { etDate.error = "Tanggal wajib diisi"; return }

        val updatedEvent = Event(
            id = eventId,
            name = name,
            status = status,
            description = description,
            priceRange = price,
            posterUrl = poster,
            date = date
        )

        btnSave.isEnabled = false
        Toast.makeText(context, "⏳ Memperbarui data Event...", Toast.LENGTH_SHORT).show()

        database.child(eventId!!).setValue(updatedEvent)
            .addOnSuccessListener {
                btnSave.isEnabled = true
                Toast.makeText(context, "✅ Event $name berhasil diperbarui!", Toast.LENGTH_LONG).show()
                Log.i(TAG, "Event berhasil diperbarui: $eventId")

                // NAVIGASI BALIK: Keluar dari Fragment Edit dan kembali ke EventManagementFragment
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { error ->
                btnSave.isEnabled = true
                val errorMessage = error.message ?: "Kesalahan tak teridentifikasi."
                Toast.makeText(context, "❌ GAGAL FIREBASE: $errorMessage", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Firebase Update Failure: $errorMessage", error)
            }
    }
}