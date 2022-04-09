package com.norbertzombori.produmax.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.EventAdapter
import com.norbertzombori.produmax.data.Event
import com.norbertzombori.produmax.data.User
import com.norbertzombori.produmax.viewmodels.FriendsViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.recycler_view

class ProfileFragment : Fragment(R.layout.fragment_profile), EventAdapter.OnItemClickListener {
    private val viewModel: FriendsViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: MutableList<Event>
    private lateinit var eventAdapter: EventAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Profile")
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)


        eventList = mutableListOf()
        eventAdapter = EventAdapter(eventList, this)
        recyclerView.adapter = eventAdapter



        tv_profile_name.text = viewModel.selected.value?.displayName
        viewModel.selected.value?.displayName?.let { findUserById(it) }
    }

    fun findUserById(username: String) {
        val docRef = viewModel.appRepository.db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if(currentUser.displayName == username){
                            when(currentUser.profileVisibility){
                                true -> eventChangeListener(document.id)
                                false -> tv_profile_name.text = viewModel.selected.value?.displayName + "This profile is private"
                            }
                        }
                    }
                } else {

                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
    }

    private fun eventChangeListener(userId: String) {
        viewModel.appRepository.db.collection("users").document(userId)
            .collection("events")
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        eventList.add(dc.document.toObject(Event::class.java))
                        eventAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}

