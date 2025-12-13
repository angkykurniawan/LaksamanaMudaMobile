package com.example.eventmanagement.event.doc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.eventmanagement.R
import com.example.eventmanagement.databinding.FragmentAddLinkBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddLinkFragment : Fragment() {

    private var _binding: FragmentAddLinkBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var eventId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menerima ID Event dari Argumen
        eventId = arguments?.getString("EVENT_ID")

        if (eventId == null) {
            Toast.makeText(context, "Error: Event ID tidak ditemukan.", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }
        database = FirebaseDatabase.getInstance().getReference("documentation").child(eventId!!)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnSave.setOnClickListener { handleSaveLink() }
        binding.btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun handleSaveLink() {
        val name = binding.etLinkName.text.toString().trim()
        val url = binding.etLinkUrl.text.toString().trim()

        if (name.isEmpty() || url.isEmpty()) {
            Toast.makeText(context, "Nama dan Link wajib diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        saveLinkToFirebase(name, url)
    }

    private fun saveLinkToFirebase(name: String, url: String) {
        val linkId = database.push().key

        if (linkId == null) {
            Toast.makeText(context, "Gagal membuat Link ID.", Toast.LENGTH_LONG).show()
            return
        }

        val newLink = Link(
            id = linkId,
            eventId = eventId,
            name = name,
            url = url
        )

        database.child(linkId).setValue(newLink)
            .addOnSuccessListener {
                Toast.makeText(context, "Link berhasil ditambahkan!", Toast.LENGTH_SHORT).show()

                // INI ADALAH KODE YANG BENAR UNTUK KEMBALI KE DOCUMENTATIONMANAGEMENTFRAGMENT
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal menyimpan Link.", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}