package com.example.eventmanagement.adapters

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

class EventAdapter(private var eventList: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    fun updateList(newList: List<Event>) {
        eventList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgPoster: ImageView = itemView.findViewById(R.id.img_event_poster)
        private val tvName: TextView = itemView.findViewById(R.id.tv_event_name)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_event_date)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_event_price)
        private val btnAction: Button = itemView.findViewById(R.id.btn_action)
        private val btnInfo: ImageButton = itemView.findViewById(R.id.btn_info)

        fun bind(event: Event) {
            tvName.text = event.name
            tvDate.text = "🗓 ${event.date}"
            tvPrice.text = "💰 ${event.price}"

            // KOREKSI UTAMA: Menggunakan Glide untuk memuat gambar dari String (URL)
            // Asumsi: Property 'poster' sekarang adalah String (URL/Path)
            Glide.with(itemView.context)
                .load(event.poster) // Menggunakan event.poster (yang merupakan String)
                // Opsi: Anda bisa menambahkan placeholder atau error image di sini
                // .placeholder(R.drawable.placeholder_image)
                .into(imgPoster)

            btnAction.setOnClickListener {
                showPopupMenu(it, event.id)
            }

            btnInfo.setOnClickListener {
                Toast.makeText(itemView.context,
                    "${event.name}:\nStatus: ${event.status}\nDeskripsi: ${event.description}",
                    Toast.LENGTH_LONG).show()
            }
        }

        private fun showPopupMenu(view: View, eventId: Int) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.menu_event_action, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                val action = when (menuItem.itemId) {
                    R.id.action_crew -> "Kelola Crew"
                    R.id.action_notification -> "Kirim Notifikasi"
                    R.id.action_documentation -> "Lihat Dokumentasi"
                    R.id.action_engagement -> "Lihat Engagement"
                    R.id.action_edit -> "Edit"
                    R.id.action_delete -> "Hapus"
                    else -> ""
                }
                Toast.makeText(view.context, "$action Event $eventId", Toast.LENGTH_SHORT).show()
                true
            }
            popup.show()
        }
    }
}