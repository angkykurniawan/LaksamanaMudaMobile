package com.example.eventmanagement.team

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagement.R

class CrewAdapter(
    private val crewList: ArrayList<Crew>, // Crew di-resolve
    private val onEditClick: (Crew) -> Unit,
    private val onDeleteClick: (Crew) -> Unit
) : RecyclerView.Adapter<CrewAdapter.CrewViewHolder>() {

    class CrewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCrewName)
        val tvRole: TextView = itemView.findViewById(R.id.tvRole)
        val tvBirthDate: TextView = itemView.findViewById(R.id.tvBirthDate)
        val tvJoinDate: TextView = itemView.findViewById(R.id.tvJoinDate)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crew, parent, false)
        return CrewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        val currentCrew = crewList[position]

        holder.tvName.text = currentCrew.name
        holder.tvRole.text = "Role: ${currentCrew.role}"
        holder.tvBirthDate.text = currentCrew.birthDate
        holder.tvJoinDate.text = currentCrew.joinDate
        holder.tvEmail.text = currentCrew.email
        holder.tvPhone.text = currentCrew.phone // Set Phone

        holder.btnEdit.setOnClickListener {
            onEditClick(currentCrew)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(currentCrew)
        }
    }

    override fun getItemCount(): Int {
        return crewList.size
    }
}