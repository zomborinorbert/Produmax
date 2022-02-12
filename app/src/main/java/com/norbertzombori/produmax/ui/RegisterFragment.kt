package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.MainActivity
import com.norbertzombori.produmax.R
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_register_confirm.setOnClickListener{
            val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
            findNavController().navigate(action)

            (activity as MainActivity).registerUser(edit_text_register_email.text.toString(), edit_text_register_password.text.toString())
        }


    }
}