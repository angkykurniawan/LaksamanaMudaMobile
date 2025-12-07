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
        // Anda perlu memastikan R.drawable.poster_music, dll. tersedia di folder res/drawable
        return listOf(
            Event(1, "Music Festival Laksamana Muda 2025", "19 Desember 2025", "Rp. 175.000 - 300.000", R.drawable.fight, "Upcoming"),
            Event(2, "HALLOWEEN Karaoke Night", "24 Oktober 2025", "Rp. 50.000", R.drawable.fight, "Upcoming"),
            Event(3, "Jazz on the Beach", "15 Juli 2025", "Rp. 100.000", R.drawable.fight, "Pending"),
            Event(4, "New Year Party 2024", "31 Desember 2024", "Gratis", R.drawable.fight, "History")
        )
    }
}