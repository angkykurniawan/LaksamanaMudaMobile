package com.example.eventmanagement.event.eventCrew

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventmanagement.R
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.eventmanagement.databinding.FragmentAddCrewBinding
import com.example.eventmanagement.team.Crew
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.android.material.R as R_material

class AddCrewFragment : Fragment() {

    private var _binding: FragmentAddCrewBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventCrewDatabase: DatabaseReference
    private lateinit var globalCrewDatabase: DatabaseReference
    private var eventId: String? = null

    private val globalCrewList = mutableListOf<Crew>()
    private val dynamicInputs = mutableListOf<AutoCompleteTextView>()
    private lateinit var dropdownAdapter: ArrayAdapter<String>

    private val TAG = "ADD_CREW_FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCrewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventId = arguments?.getString("EVENT_ID")

        if (eventId == null) {
            Toast.makeText(context, "Error: Event ID tidak ditemukan.", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }

        eventCrewDatabase = FirebaseDatabase.getInstance().getReference("crews").child(eventId!!)

        // PERBAIKAN FATAL LEVEL 1: Mengubah jalur pengambilan data Crew Global
        // Menggunakan "crew" sesuai dengan struktur database di screenshot
        globalCrewDatabase = FirebaseDatabase.getInstance().getReference("crew")

        fetchGlobalCrews()
        setupListeners()
    }

    private fun fetchGlobalCrews() {
        globalCrewDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                globalCrewList.clear()
                for (crewSnapshot in snapshot.children) {
                    val crew = crewSnapshot.getValue(Crew::class.java)
                    if (crew != null) {
                        globalCrewList.add(crew)
                    }
                }
                Log.d(TAG, "Jumlah Crew Global dimuat: ${globalCrewList.size}")
                setupDropdowns()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Gagal memuat Crew Global: ${error.message}")
                Toast.makeText(context, "Gagal memuat Crew Global.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupDropdowns() {
        val crewNames = globalCrewList.map { it.name ?: "N/A" }
        Log.d(TAG, "Daftar Nama Crew: $crewNames")

        dropdownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            crewNames
        )

        applyDropdownSettings(binding.etCrew1)
        applyDropdownSettings(binding.etCrew2)
        applyDropdownSettings(binding.etCrew3)
    }

    private fun applyDropdownSettings(autoCompleteTextView: AutoCompleteTextView) {
        autoCompleteTextView.setAdapter(dropdownAdapter)
        autoCompleteTextView.threshold = 0

        autoCompleteTextView.inputType = InputType.TYPE_NULL
        autoCompleteTextView.keyListener = null

        autoCompleteTextView.isFocusable = true
        autoCompleteTextView.isFocusableInTouchMode = false

        // Memaksa dropdown tampil saat di-tap
        autoCompleteTextView.setOnClickListener {
            autoCompleteTextView.showDropDown()
        }

        // Memaksa dropdown tampil saat fokus
        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as AutoCompleteTextView).showDropDown()
            }
        }
    }

    private fun addNewCrewInput() {
        val context = requireContext()
        val dpToPx = { dp: Int -> (dp * resources.displayMetrics.density).toInt() }
        val inputCount = 3 + dynamicInputs.size + 1

        val label = TextView(context).apply {
            text = "Pilih Nama Crew $inputCount"
            textSize = 16f
            setTextAppearance(context, R_material.style.TextAppearance_MaterialComponents_Body1)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(16)
            }
        }

        val textInputLayout = TextInputLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
                bottomMargin = dpToPx(16)
            }

            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            isEndIconVisible = true
            endIconDrawable = context.getDrawable(R_material.drawable.mtrl_dropdown_arrow)

            hint = "Pilih Crew $inputCount"
        }

        val autoCompleteTextView = AutoCompleteTextView(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            applyDropdownSettings(this)
        }

        textInputLayout.addView(autoCompleteTextView)

        val container = binding.containerCrewInputs
        val lastChildIndex = container.childCount - 1

        container.addView(label, lastChildIndex)
        container.addView(textInputLayout, lastChildIndex + 1)

        dynamicInputs.add(autoCompleteTextView)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnAdd.setOnClickListener { handleSaveMultipleCrews() }

        binding.btnAddMoreCrew.setOnClickListener { addNewCrewInput() }
    }

    private fun handleSaveMultipleCrews() {
        val allInputs = mutableListOf<AutoCompleteTextView>()
        allInputs.add(binding.etCrew1)
        allInputs.add(binding.etCrew2)
        allInputs.add(binding.etCrew3)
        allInputs.addAll(dynamicInputs)

        val crewsToSave = allInputs
            .map { it.text.toString().trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .mapNotNull { name ->
                globalCrewList.find { it.name.equals(name, ignoreCase = true) }
            }

        if (crewsToSave.isEmpty()) {
            Toast.makeText(context, "Minimal satu Crew yang valid harus dipilih.", Toast.LENGTH_SHORT).show()
            return
        }

        saveCrewsToFirebase(crewsToSave)
    }

    private fun saveCrewsToFirebase(globalCrews: List<Crew>) {
        val rootRef = eventCrewDatabase.ref
        var successCount = 0
        val totalCrews = globalCrews.size

        val assignedDate = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID")).format(Date())

        globalCrews.forEach { globalCrew ->
            val crewId = globalCrew.id

            // PEMETAAN DARI GlobalCrew ke EventCrew
            val eventBoundCrew = EventCrew(
                id = globalCrew.id,
                eventId = eventId,
                name = globalCrew.name,
                assignedDate = assignedDate,
                dateOfBirth = globalCrew.birthDate,
                role = globalCrew.role,
                email = globalCrew.email
            )

            if (crewId != null) {
                rootRef.child(crewId).setValue(eventBoundCrew)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            successCount++
                        }

                        if (successCount + (totalCrews - successCount) == totalCrews) {
                            onSavingComplete(successCount, totalCrews - successCount)
                        }
                    }
            }
        }
    }

    private fun onSavingComplete(successCount: Int, failureCount: Int) {
        val message = if (successCount > 0) {
            "Berhasil menambahkan $successCount Crew ke Event." +
                    if (failureCount > 0) " ($failureCount gagal)" else ""
        } else {
            "Gagal menambahkan Crew."
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}