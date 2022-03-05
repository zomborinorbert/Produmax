package com.norbertzombori.produmax.data

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class AppRepository() {
    val userMutableLiveData = MutableLiveData<FirebaseUser>()
    val newEventLiveData = MutableLiveData(false)
    val firebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    fun login(email: String, password: String, mainActivity: FragmentActivity) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mainActivity, "Logged in successfully!", Toast.LENGTH_LONG)
                        .show()
                    userMutableLiveData.postValue(firebaseAuth.currentUser)

                } else {
                    Toast.makeText(mainActivity, "Failed to log in!", Toast.LENGTH_LONG).show()
                }
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

                    if (user != null) {
                        createUserCollection(user.uid, email, username)
                    }

                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }

                    user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
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

    fun createUserCollection(userId: String, email: String, displayName: String) {
        val user = hashMapOf(
            "email" to email,
            "displayName" to displayName
        )

        db.collection("users").document(userId).set(user)
    }

    fun createEventForUser(userId: String, eventName: String, eventDate: Date, accepted: Boolean = true) {
        val newEvent = hashMapOf(
            "eventName" to eventName,
            "eventDate" to eventDate,
            "accepted" to accepted
        )

        db.collection("users").document(userId).collection("events").add(newEvent)
    }

    fun createFriendForUser(userId: String, displayName: String, email: String, sent: Boolean = false, accepted: Boolean = false) {
        val newFriend = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "sent" to sent,
            "accepted" to accepted
        )

        db.collection("users").document(userId).collection("friends").add(newFriend)
    }

    fun createEventForUserWithName(name: String, eventName: String, eventDate: Date) {
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        var currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            createEventForUser(document.id, eventName, eventDate, false)
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

    fun changeInviteStatusForEvent(event: Event, eventId: String){
        val changedEvent = hashMapOf(
            "eventName" to event.eventName,
            "eventDate" to event.eventDate,
            "accepted" to !event.accepted
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events").document(eventId).set(changedEvent)
    }

    fun acceptInviteForEvent(event: Event){
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        var currentEvent = document.toObject(Event::class.java)
                        if (currentEvent.eventName == event.eventName) {
                            changeInviteStatusForEvent(event, document.id)
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

    fun checkForNewEvent() {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        var currentEvent = document.toObject(Event::class.java)
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

    fun addFriendForUser(name: String){
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        var currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            createFriendForUser(firebaseAuth.currentUser?.uid!!, currentUser.displayName, currentUser.email,true, false)
                            createFriendForUser(document.id, firebaseAuth.currentUser?.displayName!!, firebaseAuth.currentUser?.email!!, false, false)
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

    fun acceptFriendRequestHelper(name: String){
        acceptFriendRequest(name, firebaseAuth.currentUser?.uid!!)
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        var currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                           acceptFriendRequest(firebaseAuth.currentUser?.displayName!!, document.id)
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

    fun acceptFriendRequest(name: String, userId: String){
        val docRef = db.collection("users").document(userId).collection("friends")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        var currentFriend = document.toObject(Friend::class.java)
                        if (currentFriend.displayName == name) {
                            changeFriendStatus(userId, document.id, currentFriend.displayName, currentFriend.email, currentFriend.sent, true)
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

    fun changeFriendStatus(userId: String, eventId: String, displayName: String, email: String, sent: Boolean, accepted: Boolean) {
        val changedFriendStatus = hashMapOf(
            "accepted" to accepted,
            "displayName" to displayName,
            "sent" to sent,
            "email" to email
        )

        db.collection("users").document(userId).collection("friends").document(eventId).set(changedFriendStatus)
    }


}