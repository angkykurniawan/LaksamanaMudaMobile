package com.example.eventmanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.eventmanagement.viewmodels.EventViewModel

class StatsActionsFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
    private lateinit var tvTotalEventCount: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats_action, container, false)
        // Pastikan ID ini ada di fragment_stats_actions.xml
        tvTotalEventCount = view.findViewById(R.id.tv_total_event_count)

        // Mengamati perubahan data total event dari ViewModel
        viewModel.events.observe(viewLifecycleOwner) { events ->
            // Menampilkan total event dari daftar yang sudah difilter
            tvTotalEventCount.text = events.size.toString()
        }

        return view
    }


}