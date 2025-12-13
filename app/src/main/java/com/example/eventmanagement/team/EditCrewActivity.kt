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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditCrewActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var etJoinDate: EditText
    private lateinit var etRole: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText // Tambahkan Phone
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var database: DatabaseReference
    private var crewId: String? = null
    private val TAG = "EDIT_CREW_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asumsi layout XML untuk tampilan edit kru adalah 'activity_edit_crew'
        setContentView(R.layout.activity_edit_crew)

        database = FirebaseDatabase.getInstance().getReference("crew")
        crewId = intent.getStringExtra("CREW_ID")

        if (crewId == null) {
            Toast.makeText(this, "Error: ID Kru tidak ditemukan.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        initViews()
        loadCrewData(crewId!!)
        setupListeners()
    }

    private fun initViews() {
        // ID input field harus sesuai dengan XML activity_edit_crew
        etName = findViewById(R.id.et_name)
        etBirthDate = findViewById(R.id.et_birth_date)
        etJoinDate = findViewById(R.id.et_join_date)
        etRole = findViewById(R.id.et_role)
        etEmail = findViewById(R.id.et_email)
        etPhone = findViewById(R.id.et_phone) // Asumsi ID et_phone ada di XML

        btnSave = findViewById(R.id.btn_save_crew)
        btnCancel = findViewById(R.id.btn_cancel)

        etBirthDate.keyListener = null
        etJoinDate.keyListener = null
    }

    private fun setupListeners() {
        etBirthDate.setOnClickListener { showDatePickerDialog(etBirthDate) }
        etJoinDate.setOnClickListener { showDatePickerDialog(etJoinDate) }

        btnSave.setOnClickListener { handleUpdateCrew() }
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

    private fun loadCrewData(id: String) {
        database.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val crew = snapshot.getValue(Crew::class.java)
                if (crew != null) {
                    etName.setText(crew.name)
                    etBirthDate.setText(crew.birthDate)
                    etJoinDate.setText(crew.joinDate)
                    etRole.setText(crew.role)
                    etEmail.setText(crew.email)
                    etPhone.setText(crew.phone) // Muat Phone
                } else {
                    Toast.makeText(this@EditCrewActivity, "Data Kru tidak ditemukan.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditCrewActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleUpdateCrew() {
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

        val updatedCrew = Crew(
            id = crewId,
            name = name,
            birthDate = birthDate,
            joinDate = joinDate,
            role = role,
            email = email,
            phone = phone // Sertakan Phone
        )

        btnSave.isEnabled = false
        Toast.makeText(this, "⏳ Memperbarui data Kru...", Toast.LENGTH_SHORT).show()

        database.child(crewId!!).setValue(updatedCrew)
            .addOnSuccessListener {
                btnSave.isEnabled = true
                Toast.makeText(this, "✅ Kru $name berhasil diperbarui!", Toast.LENGTH_LONG).show()
                Log.i(TAG, "Kru berhasil diperbarui: $crewId")
                finish()
            }
            .addOnFailureListener { error ->
                btnSave.isEnabled = true
                val errorMessage = error.message ?: "Kesalahan tak teridentifikasi."
                Toast.makeText(this, "❌ GAGAL FIREBASE: $errorMessage", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Firebase Update Failure: $errorMessage", error)
            }
    }
}