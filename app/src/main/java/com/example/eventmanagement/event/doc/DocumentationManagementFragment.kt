package com.example.eventmanagement.event.doc

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
import com.example.eventmanagement.R
import com.example.eventmanagement.databinding.FragmentDocumentationManagementBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DocumentationManagementFragment : Fragment(), LinkAdapter.LinkActionListener {

    private var _binding: FragmentDocumentationManagementBinding? = null
    // Baris ini adalah baris 24 (getBinding) yang menyebabkan crash jika _binding null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var linkList: ArrayList<Link>
    private lateinit var linkAdapter: LinkAdapter
    private var eventId: String? = null
    private val TAG = "DOC_MGMT_FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentationManagementBinding.inflate(inflater, container, false)
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

        database = FirebaseDatabase.getInstance().getReference("documentation").child(eventId!!)
        linkList = arrayListOf()

        setupViews()
        setupRecyclerView()
        fetchLinkData()
    }

    private fun fetchLinkData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // PERBAIKAN KRITIS: Cek apakah view/binding masih ada sebelum mengakses UI
                val currentBinding = _binding ?: return

                linkList.clear()

                if (snapshot.exists()) {
                    for (linkSnapshot in snapshot.children) {
                        val link = linkSnapshot.getValue(Link::class.java)
                        if (link != null) {
                            linkList.add(link)
                        }
                    }
                }

                // Gunakan currentBinding yang sudah dicek null
                currentBinding.tvTotalLinkCount.text = linkList.size.toString()
                linkAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Gagal memuat data link: ${error.message}")
                // Cek null sebelum menampilkan Toast
                if (context != null) {
                    Toast.makeText(context, "Gagal memuat data.", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    // ... (Fungsi setupViews, setupRecyclerView, onEditClick, onDeleteClick, deleteLink tetap sama)
    // Catatan: Anda perlu memastikan semua fungsi ini didefinisikan secara lengkap.

    override fun onDestroyView() {
        super.onDestroyView()
        // Ini adalah baris yang menyebabkan crash di atas jika data datang setelahnya
        _binding = null
    }

    // Pastikan semua fungsi lainnya ada (contoh kerangka):
    private fun setupViews() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.cardAddLink.setOnClickListener { navigateToLinkFragment(AddLinkFragment()) }
    }

    private fun setupRecyclerView() {
        binding.rvLinkList.layoutManager = LinearLayoutManager(context)
        linkAdapter = LinkAdapter(linkList, this)
        binding.rvLinkList.adapter = linkAdapter
    }

    override fun onEditClick(link: Link) { navigateToLinkFragment(EditLinkFragment(), link.id) }

    override fun onDeleteClick(link: Link) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Link")
            .setMessage("Apakah Anda yakin ingin menghapus link '${link.name}'?")
            .setPositiveButton("Hapus") { _, _ -> deleteLink(link.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteLink(linkId: String?) {
        if (linkId == null) return
        database.child(linkId).removeValue()
            .addOnSuccessListener { Toast.makeText(context, "Link berhasil dihapus!", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(context, "Gagal menghapus link.", Toast.LENGTH_SHORT).show() }
    }

    private fun navigateToLinkFragment(fragment: Fragment, linkId: String? = null) {
        val args = Bundle().apply {
            putString("EVENT_ID", eventId)
            if (linkId != null) {
                putString("LINK_ID", linkId)
            }
        }
        fragment.arguments = args
        parentFragmentManager.beginTransaction()
            .replace(com.example.eventmanagement.R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}