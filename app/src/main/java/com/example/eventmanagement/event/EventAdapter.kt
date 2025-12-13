package com.example.eventmanagement.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Import library Glide
import com.example.eventmanagement.R
import com.example.eventmanagement.event.Event // Import data class Event
import com.example.eventmanagement.event.EventActionListener

class EventAdapter(
    private val eventList: ArrayList<Event>,
    // Menggunakan interface listener baru
    private val listener: EventActionListener
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.ivPoster)
        val tvEventName: TextView = itemView.findViewById(R.id.tvEventName)
        val tvPriceRange: TextView = itemView.findViewById(R.id.tvPriceRange)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val btnAction: Button = itemView.findViewById(R.id.btnAction)
        val btnInfo: ImageButton = itemView.findViewById(R.id.btnInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = eventList[position]
        val context = holder.itemView.context

        holder.tvEventName.text = currentEvent.name
        holder.tvPriceRange.text = currentEvent.priceRange
        holder.tvDate.text = currentEvent.date

        // Menampilkan Poster menggunakan Glide
        if (!currentEvent.posterUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(currentEvent.posterUrl)
                .placeholder(R.drawable.fight)
                .into(holder.ivPoster)
        } else {
            holder.ivPoster.setImageResource(R.drawable.fight)
        }

        // --- LOGIKA POP-UP MENU UNTUK btnAction ---
        holder.btnAction.setOnClickListener {
            val popup = PopupMenu(context, holder.btnAction)
            popup.menuInflater.inflate(R.menu.menu_event_action, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> listener.onEditClick(currentEvent)
                    R.id.action_delete -> listener.onDeleteClick(currentEvent)
                    // Aksi lain yang didelegasikan ke Fragment
                    else -> listener.onDetailActionClick(currentEvent, menuItem.itemId)
                }
                true
            }
            popup.show()
        }

        holder.btnInfo.setOnClickListener { listener.onInfoClick(currentEvent) }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}