package com.example.eventmanagement.event

interface EventActionListener {
    fun onEditClick(event: Event)
    fun onDeleteClick(event: Event)
    fun onDetailActionClick(event: Event, actionId: Int)
    fun onInfoClick(event: Event)
}