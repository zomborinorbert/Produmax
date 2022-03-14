package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.norbertzombori.produmax.R
import kotlinx.android.synthetic.main.fragment_landing.*


class LandingFragment : Fragment(R.layout.fragment_landing) {
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        btn_login.setOnClickListener{
            val action = LandingFragmentDirections.actionLandingFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        btn_register.setOnClickListener{
            val action = LandingFragmentDirections.actionLandingFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        if(auth.currentUser != null){
            val action = LandingFragmentDirections.actionLandingFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }
}