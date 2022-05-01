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
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment(R.layout.fragment_login) {
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

        btn_login_confirm.setOnClickListener {
            when {
                et_login_password.text.length > 5 && isEmailValid(et_login_email.text.toString()) -> {
                    viewModel.login(
                        et_login_email.text.toString(),
                        et_login_password.text.toString(),
                        requireActivity()
                    )
                }
                !isEmailValid(et_login_email.text.toString()) -> {
                    Toast.makeText(
                        requireActivity(),
                        "Email address is not valid!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                et_login_password.text.length < 6 -> {
                    Toast.makeText(
                        requireActivity(),
                        "Password is not long enough!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
    }

    private fun updateUI() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}