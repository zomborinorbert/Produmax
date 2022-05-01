package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.LoginRegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val viewModel: LoginRegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("")
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        val userObserver = Observer<FirebaseUser> { user ->
            if (user != null) {
                updateUI()
            }
        }
        viewModel.userMutableLiveData.observe(viewLifecycleOwner, userObserver)

        btn_register_confirm.setOnClickListener {
            if (et_register_email.text.length > 6 && et_register_password.text.length > 5 && et_register_username.text.length > 5) {
                viewModel.register(
                    et_register_email.text.toString(),
                    et_register_password.text.toString(),
                    et_register_username.text.toString(),
                    requireActivity()
                )
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Email or password is not long enough!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun updateUI() {
        val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
        findNavController().navigate(action)
    }
}