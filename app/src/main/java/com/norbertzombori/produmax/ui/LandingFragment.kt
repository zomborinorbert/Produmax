package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.R
import kotlinx.android.synthetic.main.fragment_landing.*


class LandingFragment : Fragment(R.layout.fragment_landing) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        button_login.setOnClickListener{
            val action = LandingFragmentDirections.actionLandingFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        button_register.setOnClickListener{
            val action = LandingFragmentDirections.actionLandingFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

    }
}