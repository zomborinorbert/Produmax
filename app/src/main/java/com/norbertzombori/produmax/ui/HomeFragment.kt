package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.norbertzombori.produmax.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = Firebase.auth.currentUser

        welcome_text.text = "Welcome ${user?.email}!"


        button_logout.setOnClickListener {
            Firebase.auth.signOut()
            val action = HomeFragmentDirections.actionHomeFragmentToLandingFragment()
            findNavController().navigate(action)
        }
    }
}