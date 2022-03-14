package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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

class InvitesFragment : Fragment(R.layout.fragment_invites), EventAdapter.OnItemClickListener {
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
    }

    override fun onItemClick(position: Int) {
        showAlertDialogAcceptInvite(position)
    }

    private fun eventChangeListener(userId: String) {
        viewModel.appRepository.db.collection("users").document(userId).collection("events")
            .orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED && !dc.document.toObject(Event::class.java).accepted) {
                        eventList.add(dc.document.toObject(Event::class.java))
                    }
                }

                eventAdapter.notifyDataSetChanged()
            }
    }


    private fun showAlertDialogAcceptInvite(position: Int) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Do you want to accept this invitation?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.acceptInviteForEvent(eventList[position])
                eventList.removeAt(position)
                eventAdapter.notifyDataSetChanged()
            }.setNegativeButton("No") { _, _ ->

            }.show()
    }
}