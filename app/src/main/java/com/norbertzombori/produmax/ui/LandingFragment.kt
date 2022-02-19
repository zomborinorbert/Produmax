package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.norbertzombori.produmax.R
import kotlinx.android.synthetic.main.fragment_landing.*


class LandingFragment : Fragment(R.layout.fragment_landing) {
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val TAG = "MyActivity"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        db = Firebase.firestore
        auth = Firebase.auth

        button_login.setOnClickListener{
            val action = LandingFragmentDirections.actionLandingFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        button_register.setOnClickListener{
            val action = LandingFragmentDirections.actionLandingFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        val currentUser = auth.currentUser
        if(currentUser != null){
            val action = LandingFragmentDirections.actionLandingFragmentToHomeFragment()
            findNavController().navigate(action)
        }

        button_testdb.setOnClickListener(){
            val docRef = db.collection("users").document("K1VsAtqodgRKLVArbjafXxZ85ff1")
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }
}