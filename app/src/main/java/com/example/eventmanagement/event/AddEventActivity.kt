package com.example.eventmanagement.event

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventmanagement.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEventActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var spStatus: Spinner
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var etPoster: EditText
    private lateinit var etDate: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button
    private lateinit var btnBack: ImageButton

    // Daftar status yang diizinkan (sesuai data yang akan difilter)
    private val eventStatuses = arrayOf("Upcoming", "Pending", "History", "Done")

    private lateinit var database: DatabaseReference
    private val TAG = "ADD_EVENT_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        database = FirebaseDatabase.getInstance().getReference("events")

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.et_name)
        spStatus = findViewById(R.id.sp_status) // Inisialisasi Spinner
        etDescription = findViewById(R.id.et_description)
        etPrice = findViewById(R.id.et_price)
        etPoster = findViewById(R.id.et_poster)
        etDate = findViewById(R.id.et_date)

        btnAdd = findViewById(R.id.btn_add_event)
        btnCancel = findViewById(R.id.btn_cancel)
        btnBack = findViewById(R.id.btn_back)

        etDate.keyListener = null

        // --- Setup Spinner ---
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item, // Layout tampilan dropdown
            eventStatuses // Sumber data
        )
        spStatus.adapter = adapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        etDate.setOnClickListener { showDatePickerDialog(etDate) }
        btnAdd.setOnClickListener { handleAddEvent() }
        btnCancel.setOnClickListener { finish() }
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

        DatePickerDialog(this, dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun handleAddEvent() {
        val name = etName.text.toString().trim()
        val status = spStatus.selectedItem.toString() // Ambil nilai dari Spinner
        val description = etDescription.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val poster = etPoster.text.toString().trim()
        val date = etDate.text.toString().trim()

        // --- Validasi Input ---
        if (name.isEmpty()) { etName.error = "Nama wajib diisi"; return }
        // Validasi status dihilangkan karena Spinner selalu memiliki nilai
        if (description.isEmpty()) { etDescription.error = "Deskripsi wajib diisi"; return }
        if (price.isEmpty()) { etPrice.error = "Harga wajib diisi"; return }
        if (poster.isEmpty()) { etPoster.error = "Poster URL/Nama wajib diisi"; return }
        if (date.isEmpty()) { etDate.error = "Tanggal wajib diisi"; return }

        saveEventToFirebase(name, status, description, price, poster, date)
    }

    private fun saveEventToFirebase(name: String, status: String, description: String, price: String, poster: String, date: String) {
        val eventId = database.push().key

        if (eventId == null) {
            Toast.makeText(this, "❌ GAGAL: Tidak dapat membuat ID Event unik.", Toast.LENGTH_LONG).show()
            return
        }

        val newEvent = Event(
            id = eventId,
            name = name,
            status = status,
            description = description,
            priceRange = price,
            posterUrl = poster,
            date = date
        )

        btnAdd.isEnabled = false
        Toast.makeText(this, "⏳ Menyimpan data Event...", Toast.LENGTH_SHORT).show()

        database.child(eventId).setValue(newEvent)
            .addOnSuccessListener {
                btnAdd.isEnabled = true
                Toast.makeText(this, "✅ Event $name berhasil ditambahkan!", Toast.LENGTH_LONG).show()
                Log.i(TAG, "Event berhasil disimpan dengan ID: $eventId")
                finish()
            }
            .addOnFailureListener { error ->
                btnAdd.isEnabled = true
                val errorMessage = error.message ?: "Kesalahan tak teridentifikasi."
                Toast.makeText(this, "❌ GAGAL FIREBASE: $errorMessage", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Firebase Save Failure: $errorMessage", error)
            }
    }
}