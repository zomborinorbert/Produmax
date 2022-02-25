package com.norbertzombori.produmax.ui

import android.content.ContentValues
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.EventAdapter
import com.norbertzombori.produmax.data.Event
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import kotlinx.android.synthetic.main.fragment_planner.*
import kotlinx.android.synthetic.main.fragment_planner.recycler_view

class PlannerFragment : Fragment(R.layout.fragment_planner), EventAdapter.OnItemClickListener {
    private lateinit var viewModel: CreateEventViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: MutableList<Event>
    private lateinit var eventAdapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = Firebase.auth.currentUser
        viewModel = CreateEventViewModel()
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)


        eventList = mutableListOf()
        eventAdapter = EventAdapter(eventList, this)
        recyclerView.adapter = eventAdapter

        eventChangeListener(user?.uid!!)

        button_navigate_to_create_event.setOnClickListener {
            val action = PlannerFragmentDirections.actionPlannerFragmentToCreateEventFragment()
            findNavController().navigate(action)
        }

        button_navigate_to_invites.setOnClickListener {
            val action = PlannerFragmentDirections.actionPlannerFragmentToInvitesFragment()
            findNavController().navigate(action)
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireActivity(), "Item $position clicked", Toast.LENGTH_SHORT).show()
    }

    private fun eventChangeListener(userId: String) {
        viewModel.appRepository.db.collection("users").document(userId).collection("events").orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED && dc.document.toObject(Event::class.java).accepted){
                            eventList.add(dc.document.toObject(Event::class.java))
                        }
                    }

                    eventAdapter.notifyDataSetChanged()
                }

            })

    }

}