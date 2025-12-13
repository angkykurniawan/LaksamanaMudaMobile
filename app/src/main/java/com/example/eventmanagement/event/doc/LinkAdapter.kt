package com.example.eventmanagement.event.doc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagement.R

class LinkAdapter(
    private val linkList: ArrayList<Link>,
    private val listener: LinkActionListener
) : RecyclerView.Adapter<LinkAdapter.LinkViewHolder>() {

    interface LinkActionListener {
        fun onEditClick(link: Link)
        fun onDeleteClick(link: Link)
    }

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLinkName: TextView = itemView.findViewById(R.id.tvLinkName)
        val tvLinkUrl: TextView = itemView.findViewById(R.id.tvLinkUrl)
        val btnEditLink: ImageButton = itemView.findViewById(R.id.btnEditLink)
        val btnDeleteLink: ImageButton = itemView.findViewById(R.id.btnDeleteLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_link, parent, false)
        return LinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val currentLink = linkList[position]

        holder.tvLinkName.text = currentLink.name
        holder.tvLinkUrl.text = currentLink.url

        holder.btnEditLink.setOnClickListener { listener.onEditClick(currentLink) }
        holder.btnDeleteLink.setOnClickListener { listener.onDeleteClick(currentLink) }
    }

    override fun getItemCount(): Int {
        return linkList.size
    }
}