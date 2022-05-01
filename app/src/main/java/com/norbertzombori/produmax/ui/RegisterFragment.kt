package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.util.Patterns
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
            when {
                isEmailValid(et_register_email.text.toString()) && et_register_password.text.length > 5 && et_register_username.text.length > 4 -> {
                    viewModel.register(
                        et_register_email.text.toString(),
                        et_register_password.text.toString(),
                        et_register_username.text.toString(),
                        requireActivity()
                    )
                }
                !isEmailValid(et_register_email.text.toString()) -> {
                    Toast.makeText(
                        requireActivity(),
                        "Email address is invalid!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                et_register_password.text.length < 6 -> {
                    Toast.makeText(
                        requireActivity(),
                        "Password is not long enough!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                et_register_username.text.length < 5 -> {
                    Toast.makeText(
                        requireActivity(),
                        "Username is not long enough!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    private fun updateUI() {
        val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}