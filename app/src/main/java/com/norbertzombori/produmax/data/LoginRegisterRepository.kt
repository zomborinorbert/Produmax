package com.norbertzombori.produmax.data

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class LoginRegisterRepository {
    val db = Firebase.firestore
    val userMutableLiveData = MutableLiveData<FirebaseUser>()
    val eventChecked = MutableLiveData(false)
    val newEventLiveData = MutableLiveData(false)
    val firebaseAuth = FirebaseAuth.getInstance()
    val visibilityLiveData = MutableLiveData(false)

    fun login(email: String, password: String, mainActivity: FragmentActivity) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mainActivity, "Logged in successfully!", Toast.LENGTH_LONG)
                        .show()
                    userMutableLiveData.postValue(firebaseAuth.currentUser)
                    saveLoginDay()
                } else {
                    Toast.makeText(mainActivity, "Failed to log in!", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun registerCheck(
        email: String,
        password: String,
        username: String,
        mainActivity: FragmentActivity
    ) {
        var foundUser = false
        var foundEmail = false
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == username) {
                            foundUser = true
                        }
                        if (currentUser.email == email) {
                            foundEmail = true
                        }
                    }
                    if (foundUser) {
                        Toast.makeText(
                            mainActivity,
                            "Username is already in use!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (foundEmail) {
                        Toast.makeText(mainActivity, "Email is already in use!", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        register(email, password, username, mainActivity)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }


    }

    fun register(
        email: String,
        password: String,
        username: String,
        mainActivity: FragmentActivity
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser

                    createUserCollection(user?.uid!!, email, username)

                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }

                    user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                mainActivity,
                                "Successful registration",
                                Toast.LENGTH_LONG
                            ).show()
                            userMutableLiveData.postValue(firebaseAuth.currentUser)
                        }
                    }
                } else {
                    Toast.makeText(mainActivity, "Failed to register", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun resetPassword(email: String, mainActivity: FragmentActivity) {
        var foundEmail = false
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.email == email) {
                            foundEmail = true
                        }
                    }
                    if (foundEmail) {
                        Log.d(TAG, "email found $email")
                        Firebase.auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        mainActivity,
                                        "An email has been sent to the given email address!",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "get failed with ", it)
                            }
                    } else {
                        Toast.makeText(
                            mainActivity,
                            "There is no user registered with this email address!",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }

    fun editProfileVisibility(setting: Boolean) {
        val currentDay = LocalDate.now().dayOfMonth

        val newSetting = hashMapOf(
            "email" to firebaseAuth.currentUser?.email!!,
            "displayName" to firebaseAuth.currentUser?.displayName!!,
            "profileVisibility" to setting,
            "latestLogin" to currentDay
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).set(newSetting)
    }

    fun getProfileVisibility() {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val currentUser = document.toObject(User::class.java)
                    visibilityLiveData.postValue(currentUser!!.profileVisibility)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun createUserCollection(userId: String, email: String, displayName: String) {
        val user = hashMapOf(
            "email" to email,
            "displayName" to displayName,
            "profileVisibility" to false,
            "latestLogin" to LocalDate.now().dayOfMonth
        )

        db.collection("users").document(userId).set(user)
    }


    fun saveLoginDay() {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val currentUser = document.toObject(User::class.java)
                    val currentDay = LocalDate.now().dayOfMonth
                    if (currentDay != currentUser!!.latestLogin) {
                        unCheckEveryHabit()
                    }
                    val newSetting = hashMapOf(
                        "email" to firebaseAuth.currentUser?.email!!,
                        "displayName" to firebaseAuth.currentUser?.displayName!!,
                        "profileVisibility" to currentUser!!.profileVisibility,
                        "latestLogin" to currentDay
                    )

                    db.collection("users").document(firebaseAuth.currentUser?.uid!!).set(newSetting)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun unCheckEveryHabit() {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentHabit = document.toObject(Habit::class.java)
                        unCheckHabit(document.id, currentHabit.habitDescription)
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun unCheckHabit(habitId: String, habitDescription: String) {
        val newHabitCheck = hashMapOf(
            "done" to false,
            "habitDescription" to habitDescription
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
            .document(habitId).set(newHabitCheck)
    }

    fun checkForNewEvent() {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentEvent = document.toObject(Event::class.java)
                        if (!currentEvent.accepted) {
                            newEventLiveData.postValue(true)
                        }
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun disableNewEvent() {
        newEventLiveData.postValue(false)
        eventChecked.postValue(true)
    }

}