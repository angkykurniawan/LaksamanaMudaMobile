package com.example.eventmanagement.customer

import android.os.Bundle
import android.view.View // Import ini untuk tipe View generik
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import com.example.eventmanagement.R // Pastikan ini mengarah ke file R yang benar

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

// --- 1. MODEL DATA ---

data class TicketRequest(
    val kategori_tiket: String,
    val jumlah_tiket: Int
)

data class PredictionResponse(
    val cluster: Int?,
    val engagement_level: String?,
    val jumlah_tiket: Double?,
    val kategori_tiket: String?
)

// --- 2. ACTIVITY IMPLEMENTATION ---

class CustomerEngagement : AppCompatActivity() {

    // Inisialisasi global untuk OkHttp dan Gson
    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    // Ganti dengan URL ngrok Anda yang sebenarnya
    private val PUBLIC_URL = "https://39baec8a35f7.ngrok-free.app/"
    private val URL = PUBLIC_URL + "/predict"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_engagement)

        // Penyesuaian Insets
        // FIX: Menggunakan View generik untuk menghindari ClassCastException dan memastikan ID main ada.
        val mainLayout = findViewById<View>(R.id.main)

        // Binding Insets hanya jika mainLayout ditemukan
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }


        // --- BINDING UI BARU ---

        val etKategori = findViewById<EditText>(R.id.etKategori)
        val etJumlah = findViewById<EditText>(R.id.etJumlah)
        val btnPrediksi = findViewById<Button>(R.id.btnPrediksi)
        val tvHasil = findViewById<TextView>(R.id.tvHasil)

        btnPrediksi.setOnClickListener {
            // 1. Ambil Input
            val kategori = etKategori.text.toString().trim()
            val jumlahString = etJumlah.text.toString().trim()

            // 2. Validasi Input
            if (kategori.isEmpty() || jumlahString.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua kolom input.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val jumlah: Int
            try {
                jumlah = jumlahString.toInt()
                if (jumlah <= 0) throw NumberFormatException()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Jumlah tiket harus berupa angka positif.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Persiapan Request
            val requestPayload = TicketRequest(kategori, jumlah)

            // Tampilkan pesan loading
            tvHasil.text = "Memuat prediksi... ⏳"

            // 4. Eksekusi Jaringan (Menggunakan Coroutines)
            makePrediction(requestPayload, tvHasil)
        }
    }

    // --- 3. FUNGSI JARINGAN ---

    private fun makePrediction(payload: TicketRequest, tvHasil: TextView) {
        // CoroutineScope yang terikat pada IO Dispatcher untuk operasi jaringan
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

                    // Pindah ke Main Thread untuk update UI
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && responseBody != null) {
                            try {
                                val prediction = gson.fromJson(responseBody, PredictionResponse::class.java)

                                val resultText = "✅ Prediksi Berhasil:\n" +
                                        "Level Engagement: ${prediction.engagement_level ?: "N/A"}\n" +
                                        "Cluster Pelanggan: ${prediction.cluster ?: "N/A"}"
                                tvHasil.text = resultText
                            } catch (e: Exception) {
                                tvHasil.text = "Error: Respon diterima (Status ${response.code}), tapi gagal memproses data."
                                Toast.makeText(this@CustomerEngagement, "Gagal memproses data JSON.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            tvHasil.text = "Error: Gagal menghubungi API. Status ${response.code}. Cek ngrok URL."
                            Toast.makeText(this@CustomerEngagement, "Gagal: Status ${response.code}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    tvHasil.text = "Error Jaringan: Gagal terhubung ke server."
                    Toast.makeText(this@CustomerEngagement, "Error Jaringan: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}