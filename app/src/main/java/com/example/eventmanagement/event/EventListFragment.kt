package com.example.eventmanagement.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventmanagement.R
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagement.adapters.EventAdapter
import com.example.eventmanagement.viewmodels.EventViewModel

class EventListFragment : Fragment() {
    private val viewModel: EventViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_events)

        setupRecyclerView()
        observeViewModel()

        return view
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(emptyList())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
            // Penting: Memastikan RecyclerView tidak bentrok dengan NestedScrollView di Activity
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        // Mengamati data event dari ViewModel dan memperbarui Adapter
        viewModel.events.observe(viewLifecycleOwner) { events ->
            eventAdapter.updateList(events)
        }
    }


}