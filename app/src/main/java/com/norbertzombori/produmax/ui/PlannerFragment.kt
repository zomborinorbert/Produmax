package com.norbertzombori.produmax.ui


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.EventAdapter
import com.norbertzombori.produmax.data.Event
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import kotlinx.android.synthetic.main.fragment_planner.*
import kotlinx.android.synthetic.main.fragment_planner.recycler_view

class PlannerFragment : Fragment(R.layout.fragment_planner), EventAdapter.OnItemClickListener {
    private val viewModel: CreateEventViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: MutableList<Event>
    private lateinit var eventAdapter: EventAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)


        eventList = mutableListOf()
        eventAdapter = EventAdapter(viewModel.eventList.value!!, this)
        recyclerView.adapter = eventAdapter

        viewModel.eventList.observe(viewLifecycleOwner) {
            eventAdapter.notifyDataSetChanged()
            Log.d(ContentValues.TAG, "New document added")
        }

        btn_navigate_to_create_event.setOnClickListener {
            val action = PlannerFragmentDirections.actionPlannerFragmentToCreateEventFragment()
            findNavController().navigate(action)
        }

        btn_navigate_to_invites.setOnClickListener {
            val action = PlannerFragmentDirections.actionPlannerFragmentToInvitesFragment()
            findNavController().navigate(action)
        }

        btn_navigate_to_create_flag.setOnClickListener {
            val action = PlannerFragmentDirections.actionPlannerFragmentToCreateEventFlagFragment()
            findNavController().navigate(action)
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireActivity(), "Item $position clicked", Toast.LENGTH_SHORT).show()
        viewModel.select(viewModel.eventList.value!![position])
        Log.d(ContentValues.TAG, "New document added ${viewModel.selected.value?.eventName}")
        val action = PlannerFragmentDirections.actionPlannerFragmentToEventDetailsFragment()
        findNavController().navigate(action)
    }

}