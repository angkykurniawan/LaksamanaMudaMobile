package com.example.eventmanagement.event

interface EventActionListener {
    fun onEditClick(event: Event)
    fun onDeleteClick(event: Event)
    // Listener baru untuk aksi detail
    fun onDetailActionClick(event: Event, actionId: Int)
    fun onInfoClick(event: Event)
}