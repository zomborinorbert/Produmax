package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.LoginRegisterViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var viewModel: LoginRegisterViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = LoginRegisterViewModel()

        val userObserver = Observer<FirebaseUser> { user ->
            if(user != null) {
                updateUI()
            }
        }
        viewModel.userMutableLiveData.observe(viewLifecycleOwner, userObserver)

        button_register_confirm.setOnClickListener{
            viewModel.register(edit_text_register_email.text.toString(), edit_text_register_password.text.toString(), edit_text_register_username.text.toString(), requireActivity())
        }

    }

    private fun updateUI(){
        val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
        findNavController().navigate(action)
    }
}