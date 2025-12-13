package com.example.eventmanagement.event.eventCrew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagement.R
import com.example.eventmanagement.team.Crew

class EventCrewAdapter(
    private val crewList: ArrayList<EventCrew>,
    private val listener: EventCrewActionListener
) : RecyclerView.Adapter<EventCrewAdapter.EventCrewViewHolder>() {

    class EventCrewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCrewName: TextView = itemView.findViewById(R.id.tvCrewName)
        val tvAssignedDate: TextView = itemView.findViewById(R.id.tvAssignedDate)
        val tvCrewEmail: TextView = itemView.findViewById(R.id.tvCrewEmail)
        val tvCrewDOB: TextView = itemView.findViewById(R.id.tvCrewDOB)
        val tvCrewRole: TextView = itemView.findViewById(R.id.tvCrewRole)
        val btnDeleteCrew: ImageButton = itemView.findViewById(R.id.btnDeleteCrew)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventCrewViewHolder {
        // Asumsi layout item_event_crew.xml ada
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_crew, parent, false)
        return EventCrewViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventCrewViewHolder, position: Int) {
        val currentCrew = crewList[position]

        holder.tvCrewName.text = currentCrew.name
        holder.tvAssignedDate.text = currentCrew.assignedDate
        holder.tvCrewEmail.text = currentCrew.email
        holder.tvCrewDOB.text = currentCrew.dateOfBirth
        holder.tvCrewRole.text = currentCrew.role

        holder.btnDeleteCrew.setOnClickListener { listener.onDeleteClick(currentCrew) }
    }

    override fun getItemCount(): Int {
        return crewList.size
    }
}