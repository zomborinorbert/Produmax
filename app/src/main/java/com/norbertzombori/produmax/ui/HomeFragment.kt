package com.norbertzombori.produmax.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
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
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showAlertDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = Firebase.auth.currentUser

        welcome_text.text = "Welcome ${user?.displayName}!"


        button_logout.setOnClickListener {
            Firebase.auth.signOut()
            val action = HomeFragmentDirections.actionHomeFragmentToLandingFragment()
            findNavController().navigate(action)
        }

        button_planner.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPlannerFragment()
            findNavController().navigate(action)
        }

        button_tracker.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToTrackerFragment()
            findNavController().navigate(action)
        }




    }

    fun showAlertDialog(){
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("New event invitation!")
            .setMessage("You have been invited to an event, do you want to check the invitation?")
            .setNegativeButton("Check them later"){dialog, which ->

            }.setPositiveButton("See invitations"){dialog, which ->
                val action = HomeFragmentDirections.actionHomeFragmentToInvitesFragment()
                findNavController().navigate(action)
            }.show()
    }


}