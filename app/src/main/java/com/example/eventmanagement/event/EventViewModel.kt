package com.example.eventmanagement.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventViewModel : ViewModel() {
    private val _currentFilter = MutableLiveData<String>("Upcoming")
    val currentFilter: LiveData<String> = _currentFilter

    fun setFilter(filter: String) {
        _currentFilter.value = filter
    }
}