package com.example.eventmanagement.team

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

class AddCrewActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var etJoinDate: EditText
    private lateinit var etRole: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText // Tambahkan Phone
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button

    private lateinit var database: DatabaseReference
    private val TAG = "ADD_CREW_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asumsi layout XML untuk tampilan tambah kru adalah 'activity_add_crew'
        setContentView(R.layout.activity_add_crew)

        database = FirebaseDatabase.getInstance().getReference("crew")

        initViews()
        setupListeners()
    }

    private fun initViews() {
        // ID input field harus sesuai dengan XML activity_add_crew
        etName = findViewById(R.id.et_name)
        etBirthDate = findViewById(R.id.et_birth_date)
        etJoinDate = findViewById(R.id.et_join_date)
        etRole = findViewById(R.id.et_role)
        etEmail = findViewById(R.id.et_email)
        etPhone = findViewById(R.id.et_phone) // Asumsi ID et_phone ada di XML

        btnAdd = findViewById(R.id.btn_add_crew)
        btnCancel = findViewById(R.id.btn_cancel)

        etBirthDate.keyListener = null
        etJoinDate.keyListener = null
    }

    private fun setupListeners() {
        etBirthDate.setOnClickListener { showDatePickerDialog(etBirthDate) }
        etJoinDate.setOnClickListener { showDatePickerDialog(etJoinDate) }

        btnAdd.setOnClickListener { handleAddCrew() }
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

    private fun handleAddCrew() {
        val name = etName.text.toString().trim()
        val birthDate = etBirthDate.text.toString().trim()
        val joinDate = etJoinDate.text.toString().trim()
        val role = etRole.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim() // Ambil Phone

        // --- Validasi Input ---
        if (name.isEmpty()) { etName.error = "Nama wajib diisi"; return }
        if (birthDate.isEmpty()) { etBirthDate.error = "Tanggal Lahir wajib diisi"; return }
        if (joinDate.isEmpty()) { etJoinDate.error = "Tanggal Gabung wajib diisi"; return }
        if (role.isEmpty()) { etRole.error = "Role wajib diisi"; return }
        if (email.isEmpty()) { etEmail.error = "Email wajib diisi"; return }
        if (phone.isEmpty()) { etPhone.error = "Nomor HP wajib diisi"; return }

        // Lanjutkan ke proses penyimpanan
        saveCrewToFirebase(name, birthDate, joinDate, role, email, phone)
    }

    private fun saveCrewToFirebase(name: String, birthDate: String, joinDate: String, role: String, email: String, phone: String) {
        val crewId = database.push().key

        if (crewId == null) {
            Toast.makeText(this, "❌ GAGAL: Tidak dapat membuat ID Kru unik.", Toast.LENGTH_LONG).show()
            return
        }

        val newCrew = Crew(
            id = crewId,
            name = name,
            birthDate = birthDate,
            joinDate = joinDate,
            role = role,
            email = email,
            phone = phone // Sertakan Phone
        )

        btnAdd.isEnabled = false
        Toast.makeText(this, "⏳ Menyimpan data Kru...", Toast.LENGTH_SHORT).show()

        database.child(crewId).setValue(newCrew)
            .addOnSuccessListener {
                btnAdd.isEnabled = true
                Toast.makeText(this, "✅ Kru $name berhasil ditambahkan!", Toast.LENGTH_LONG).show()
                Log.i(TAG, "Kru berhasil disimpan dengan ID: $crewId")
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