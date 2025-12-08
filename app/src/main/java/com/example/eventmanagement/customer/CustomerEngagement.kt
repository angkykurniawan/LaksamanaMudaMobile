package com.example.eventmanagement.customer

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
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
// 2. ACTIVITY IMPLEMENTATION
// ===============================

class CustomerEngagement : AppCompatActivity() {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    // ✅ GANTI SESUAI URL NGROK AKTIF KAMU
    private val PUBLIC_URL = "https://f0ee0199aa2e.ngrok-free.app"
    private val URL = "$PUBLIC_URL/predict"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_engagement)

        val mainLayout = findViewById<View>(R.id.main)
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // ===============================
        // BINDING UI
        // ===============================

        val etKategori = findViewById<EditText>(R.id.etKategori)
        val etJumlah = findViewById<EditText>(R.id.etJumlah)
        val etSumberInfo = findViewById<EditText>(R.id.etSumberInfo)
        val btnPrediksi = findViewById<Button>(R.id.btnPrediksi)
        val tvHasil = findViewById<TextView>(R.id.tvHasil)

        btnPrediksi.setOnClickListener {
            val kategori = etKategori.text.toString().trim()
            val jumlahString = etJumlah.text.toString().trim()
            val sumberInfo = etSumberInfo.text.toString().trim()

            if (kategori.isEmpty() || jumlahString.isEmpty() || sumberInfo.isEmpty()) {
                Toast.makeText(this, "Semua input wajib diisi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val jumlah: Int
            try {
                jumlah = jumlahString.toInt()
                if (jumlah <= 0) throw NumberFormatException()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Jumlah tiket harus angka positif.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val requestPayload = TicketRequest(
                kategori_tiket = kategori,
                jumlah_tiket = jumlah,
                sumber_info = sumberInfo
            )

            tvHasil.text = "Memuat prediksi... ⏳"
            makePrediction(requestPayload, tvHasil)
        }
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

                                val resultText = """
                                    ✅ Prediksi Berhasil:
                                    Kategori: ${prediction.kategori_tiket ?: "-"}
                                    Sumber Info: ${prediction.sumber_info ?: "-"}
                                    Jumlah Tiket: ${prediction.jumlah_tiket ?: "-"}
                                    Cluster: ${prediction.cluster ?: "-"}
                                    Engagement Level: ${prediction.engagement_level ?: "-"}
                                """.trimIndent()

                                tvHasil.text = resultText
                            } catch (e: Exception) {
                                tvHasil.text = "Gagal membaca respon server."
                                Toast.makeText(this@CustomerEngagement, "JSON Error", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            tvHasil.text = "API Error: ${response.code}"
                            Toast.makeText(this@CustomerEngagement, "Server Error ${response.code}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    tvHasil.text = "Gagal terhubung ke server"
                    Toast.makeText(this@CustomerEngagement, "Jaringan Bermasalah", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
