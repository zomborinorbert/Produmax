package com.norbertzombori.produmax.data

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AppRepository() {
    val userMutableLiveData = MutableLiveData<FirebaseUser>()
    val firebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, mainActivity: FragmentActivity) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mainActivity,"Logged in successfully!",Toast.LENGTH_LONG).show()
                    userMutableLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    Toast.makeText(mainActivity,"Failed to log in!",Toast.LENGTH_LONG).show()
                }
            }
    }

    fun register(email: String, password: String, mainActivity: FragmentActivity) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mainActivity,"Successful registration",Toast.LENGTH_LONG).show()
                    userMutableLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    Toast.makeText(mainActivity,"Failed to register",Toast.LENGTH_LONG).show()
                }
            }
    }
}