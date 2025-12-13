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

class EventAdapter(
    private val eventList: ArrayList<Event>,
    private val onActionClick: (Event) -> Unit,
    private val onInfoClick: (Event) -> Unit
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
        // BARIS PENGATURAN tvStatus.text DIHAPUS

        // Menampilkan Poster menggunakan Glide
        if (!currentEvent.posterUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(currentEvent.posterUrl)
                .placeholder(R.drawable.fight)
                .into(holder.ivPoster)
        } else {
            holder.ivPoster.setImageResource(R.drawable.fight)
        }

        // LOGIKA STATUS DAN WARNA DIHAPUS, SEHINGGA tvStatus AKAN SELALU MENGAMBIL NILAI DEFAULT DARI XML (visibility=GONE)
        /*
        currentEvent.status?.let { status ->
            holder.tvStatus.visibility = View.VISIBLE
            holder.tvStatus.text = status.uppercase() // Baris ini dihapus
            when (status.lowercase()) {
                "upcoming" -> holder.tvStatus.setBackgroundColor(Color.parseColor("#FF9800")) // Orange
                "pending" -> holder.tvStatus.setBackgroundColor(Color.parseColor("#2196F3")) // Blue
                "history", "done" -> holder.tvStatus.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                else -> holder.tvStatus.visibility = View.GONE
            }
        } ?: run {
            holder.tvStatus.visibility = View.GONE
        }
        */


        holder.btnAction.setOnClickListener { onActionClick(currentEvent) }
        holder.btnInfo.setOnClickListener { onInfoClick(currentEvent) }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}