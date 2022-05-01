package com.norbertzombori.produmax.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var viewModel: HomeViewModel = HomeViewModel()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().setTitle("Home")
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        setHasOptionsMenu(true)

        viewModel.checkForNewEvent()

        viewModel.checkNewDayForTracker()

        tv_welcome_text.text = "Hello, \n${viewModel.appRepository.firebaseAuth.currentUser?.displayName}"

        val eventObserver = Observer<Boolean> { value ->
            if(value) {
                showAlertDialog()
            }
        }

        viewModel.appRepository.newEventLiveData.observe(viewLifecycleOwner, eventObserver)


        btn_planner.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPlannerFragment()
            findNavController().navigate(action)
        }

        btn_tracker.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToTrackerFragment()
            findNavController().navigate(action)
        }

        btn_friends.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFriendsFragment()
            findNavController().navigate(action)
        }

        btn_todos.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToToDoFragment()
            findNavController().navigate(action)
        }
    }

    private fun showAlertDialog(){
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("New event invitation!")
            .setMessage("You have been invited to an event, do you want to check the invitation?")
            .setNegativeButton("Check them later"){ _, _ ->
                viewModel.appRepository.disableNewEvent()
            }.setPositiveButton("See invitations"){ _, _ ->
                viewModel.appRepository.disableNewEvent()
                val action = HomeFragmentDirections.actionHomeFragmentToInvitesFragment()
                findNavController().navigate(action)
            }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.SettingsFragment){
            val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
            findNavController().navigate(action)
            return true
        }else if(item.itemId == R.id.logout_menu){
            Firebase.auth.signOut()
            val action = HomeFragmentDirections.actionHomeFragmentToLandingFragment()
            findNavController().navigate(action)
            return true
        }
        return true
    }

}