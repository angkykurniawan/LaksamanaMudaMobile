package com.example.eventmanagement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventmanagement.R
import com.example.eventmanagement.event.Event

class EventViewModel : ViewModel() {

    private val allEvents = createDummyEvents()
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _currentFilter = MutableLiveData<String>()
    val currentFilter: LiveData<String> = _currentFilter

    init {
        _currentFilter.value = "Upcoming" // Filter default
        applyFilter()
    }

    fun setFilter(filter: String) {
        if (_currentFilter.value != filter) {
            _currentFilter.value = filter
            applyFilter()
        }
    }

    private fun applyFilter() {
        val filter = _currentFilter.value ?: "Upcoming"
        val filteredList = allEvents.filter { it.status == filter }
        _events.value = filteredList
    }

    // Fungsi Dummy Data
    private fun createDummyEvents(): List<Event> {
        // PERBAIKAN: Menggunakan format ISO 8601 lengkap (String)
        val isoFormatTime = "T00:00:00.000Z"

        // Asumsi: Kita menggunakan URL gambar dummy karena 'poster' sekarang String.
        val dummyImageUrl = "https://example.com/images/poster_fight.jpg"

        return listOf(
            Event(
                id = 1,
                name = "Music Festival Laksamana Muda 2025",
                status = "Upcoming",
                description = "Festival musik outdoor terbesar tahun ini di Pekanbaru.",
                price = "Rp. 175.000 - 300.000",
                // KOREKSI: Menggunakan String (URL) untuk poster
                poster = dummyImageUrl,
                date = "2025-12-19$isoFormatTime"
            ),
            Event(
                id = 2,
                name = "HALLOWEEN Karaoke Night",
                status = "Upcoming",
                description = "Malam karaoke bertema horor dengan hadiah kostum terbaik.",
                price = "Rp. 50.000",
                // KOREKSI: Menggunakan String (URL) untuk poster
                poster = dummyImageUrl,
                date = "2025-10-24$isoFormatTime"
            ),
            Event(
                id = 3,
                name = "Jazz on the Beach",
                status = "Pending",
                description = "Sesi jazz santai di tepi pantai yang indah.",
                price = "Rp. 100.000",
                // KOREKSI: Menggunakan String (URL) untuk poster
                poster = dummyImageUrl,
                date = "2025-07-15$isoFormatTime"
            ),
            Event(
                id = 4,
                name = "New Year Party 2024",
                status = "History",
                description = "Pesta perayaan tahun baru spektakuler.",
                price = "Gratis",
                // KOREKSI: Menggunakan String (URL) untuk poster
                poster = dummyImageUrl,
                date = "2024-12-31$isoFormatTime"
            )
        )
    }
}