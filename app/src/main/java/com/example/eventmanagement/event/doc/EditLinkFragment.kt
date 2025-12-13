package com.example.eventmanagement.event.doc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.eventmanagement.R
import com.example.eventmanagement.databinding.FragmentEditLinkBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditLinkFragment : Fragment() {

    private var _binding: FragmentEditLinkBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var eventId: String? = null
    private var linkId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditLinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menerima ID Event dan Link dari Argumen
        eventId = arguments?.getString("EVENT_ID")
        linkId = arguments?.getString("LINK_ID")

        if (eventId == null || linkId == null) {
            Toast.makeText(context, "Error: ID tidak lengkap.", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }

        database = FirebaseDatabase.getInstance().getReference("documentation").child(eventId!!)

        loadLinkData(linkId!!)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnSave.setOnClickListener { handleUpdateLink() }
        binding.btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun loadLinkData(id: String) {
        database.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val link = snapshot.getValue(Link::class.java)
                if (link != null) {
                    binding.etLinkName.setText(link.name)
                    binding.etLinkUrl.setText(link.url)
                } else {
                    Toast.makeText(context, "Link tidak ditemukan.", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleUpdateLink() {
        val name = binding.etLinkName.text.toString().trim()
        val url = binding.etLinkUrl.text.toString().trim()

        if (name.isEmpty() || url.isEmpty()) {
            Toast.makeText(context, "Nama dan Link wajib diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        updateLinkInFirebase(name, url)
    }

    private fun updateLinkInFirebase(name: String, url: String) {
        val updatedLink = Link(
            id = linkId,
            eventId = eventId,
            name = name,
            url = url
        )

        database.child(linkId!!).setValue(updatedLink)
            .addOnSuccessListener {
                Toast.makeText(context, "Link berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Kembali ke DocumentationManagementFragment
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal memperbarui Link.", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}