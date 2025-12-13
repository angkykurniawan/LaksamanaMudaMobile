package com.example.eventmanagement.event.eventCrew

data class EventCrew(
    var id: String? = null,
    var eventId: String? = null,
    var name: String? = null,
    var assignedDate: String? = null, // Tanggal mulai bertugas/dibuat di Event ini (sesuai mockup)
    var dateOfBirth: String? = null,
    var role: String? = null,
    var email: String? = null
)