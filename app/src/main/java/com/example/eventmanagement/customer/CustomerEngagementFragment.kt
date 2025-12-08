package com.example.eventmanagement.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.eventmanagement.R
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

// ===============================
// 1. DATA MODEL REQUEST & RESPONSE
// ===============================
data class TicketRequest(
    val kategori_tiket: String,
    val jumlah_tiket: Int,
    val sumber_info: String
)

data class PredictionResponse(
    val cluster: Int?,
    val engagement_level: String?,
    val jumlah_tiket: Double?,
    val kategori_tiket: String?,
    val sumber_info: String?
)

// ===============================
// 2. FRAGMENT IMPLEMENTATION
// ===============================
class CustomerEngagementFragment : Fragment() {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val PUBLIC_URL = "https://f0ee0199aa2e.ngrok-free.app"
    private val URL = "$PUBLIC_URL/predict"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_engagement, container, false)

        // Apply edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ===============================
        // BINDING UI
        // ===============================
        val etKategori = view.findViewById<EditText>(R.id.etKategori)
        val etJumlah = view.findViewById<EditText>(R.id.etJumlah)
        val etSumberInfo = view.findViewById<EditText>(R.id.etSumberInfo)
        val btnPrediksi = view.findViewById<Button>(R.id.btnPrediksi)
        val tvHasil = view.findViewById<TextView>(R.id.tvHasil)

        // Scrollable TextView
        tvHasil.apply {
            isVerticalScrollBarEnabled = true
            setTextIsSelectable(true)
            typeface = android.graphics.Typeface.MONOSPACE
            setLineSpacing(2f, 1.0f)
        }

        btnPrediksi.setOnClickListener {
            val kategori = etKategori.text.toString().trim()
            val jumlahString = etJumlah.text.toString().trim()
            val sumberInfo = etSumberInfo.text.toString().trim()

            if (kategori.isEmpty() || jumlahString.isEmpty() || sumberInfo.isEmpty()) {
                Toast.makeText(requireContext(), "Semua input wajib diisi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val jumlah: Int
            try {
                jumlah = jumlahString.toInt()
                if (jumlah <= 0) throw NumberFormatException()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Jumlah tiket harus angka positif.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val requestPayload = TicketRequest(kategori, jumlah, sumberInfo)
            tvHasil.text = "⏳ Memuat prediksi..."
            makePrediction(requestPayload, tvHasil)
        }

        return view
    }

    // ===============================
    // 3. FUNGSI HIT NETWORK
    // ===============================
    private fun makePrediction(payload: TicketRequest, tvHasil: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonPayload = gson.toJson(payload)
                val body = jsonPayload.toRequestBody(JSON)

                val request = Request.Builder()
                    .url(URL)
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && responseBody != null) {
                            try {
                                val prediction = gson.fromJson(responseBody, PredictionResponse::class.java)
                                val resultText = buildString {
                                    append("✅ Prediksi Customer Engagement\n\n")
                                    append(String.format("• %-20s: %s\n", "Kategori Tiket", prediction.kategori_tiket ?: "-"))
                                    append(String.format("• %-20s: %s\n", "Sumber Info", prediction.sumber_info ?: "-"))
                                    append(String.format("• %-20s: %s\n", "Jumlah Tiket", prediction.jumlah_tiket ?: "-"))
                                    append(String.format("• %-20s: %s\n", "Cluster", prediction.cluster ?: "-"))
                                    append(String.format("• %-20s: %s\n", "Engagement Level", prediction.engagement_level ?: "-"))
                                }
                                tvHasil.text = resultText
                            } catch (e: Exception) {
                                tvHasil.text = "⚠️ Gagal membaca respon server."
                                Toast.makeText(requireContext(), "JSON Error", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            tvHasil.text = "⚠️ API Error: ${response.code}"
                            Toast.makeText(requireContext(), "Server Error ${response.code}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    tvHasil.text = "⚠️ Gagal terhubung ke server"
                    Toast.makeText(requireContext(), "Jaringan bermasalah", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
