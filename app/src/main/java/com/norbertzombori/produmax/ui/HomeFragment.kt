package com.norbertzombori.produmax.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var viewModel: HomeViewModel = HomeViewModel()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.appRepository.checkForNewEvent()

        tv_welcome_text.text = "Welcome ${viewModel.appRepository.firebaseAuth.currentUser?.displayName}!"

        val eventObserver = Observer<Boolean> { value ->
            if(value) {
                showAlertDialog()
            }
        }

        viewModel.appRepository.newEventLiveData.observe(viewLifecycleOwner, eventObserver)

        btn_logout.setOnClickListener {
            Firebase.auth.signOut()
            val action = HomeFragmentDirections.actionHomeFragmentToLandingFragment()
            findNavController().navigate(action)
        }

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

    }

    private fun showAlertDialog(){
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("New event invitation!")
            .setMessage("You have been invited to an event, do you want to check the invitation?")
            .setNegativeButton("Check them later"){ _, _ ->

            }.setPositiveButton("See invitations"){ _, _ ->
                val action = HomeFragmentDirections.actionHomeFragmentToInvitesFragment()
                findNavController().navigate(action)
            }.show()
    }


}