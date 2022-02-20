package com.norbertzombori.produmax.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.EventAdapter
import com.norbertzombori.produmax.data.Event
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {
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
        eventAdapter = EventAdapter(eventList)
        recyclerView.adapter = eventAdapter

        eventChangeListener(user?.uid!!)

        welcome_text.text = "Welcome ${user?.displayName}!"


        button_logout.setOnClickListener {
            Firebase.auth.signOut()
            val action = HomeFragmentDirections.actionHomeFragmentToLandingFragment()
            findNavController().navigate(action)
        }

        //button_create_event.setOnClickListener{
        //    viewModel.createEvent(user?.uid!!, Event("TestEvent2","TestDate2"))
        //}

        button_planner.setOnClickListener{
            val action = HomeFragmentDirections.actionHomeFragmentToPlannerFragment()
            findNavController().navigate(action)
        }

        button_tracker.setOnClickListener{
            val action = HomeFragmentDirections.actionHomeFragmentToTrackerFragment()
            findNavController().navigate(action)
        }

        /*
         button_log_events.setOnClickListener(){
            val docRef = viewModel.appRepository.db.collection("users").document(user?.uid!!).collection("events")
            docRef.get()
                .addOnSuccessListener { documents ->
                    if (documents != null) {
                        for (document in documents) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }*/


    }

    private fun eventChangeListener(userId: String) {
        viewModel.appRepository.db.collection("users").document(userId).collection("events").orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
                            eventList.add(dc.document.toObject(Event::class.java))

                            //val asd = dc.document.toObject(Event::class.java)
                            //eventList.add(asd)
                            Log.d(TAG, "asdasdasdas${dc.document}")

                        }
                    }

                    eventAdapter.notifyDataSetChanged()
                }

            })


    }


}