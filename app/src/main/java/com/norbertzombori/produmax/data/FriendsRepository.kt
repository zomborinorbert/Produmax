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

class FriendsRepository {
    val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()

    fun checkIfUserExists(name: String, mainActivity: FragmentActivity) {
        if (firebaseAuth.currentUser?.displayName!! == name) {
            Toast.makeText(mainActivity, "You cannot add yourself!", Toast.LENGTH_LONG).show()
            return
        }
        var foundUser = false
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            addFriendForUser(name)
                            foundUser = true
                        }
                    }
                    if (!foundUser) {
                        Toast.makeText(mainActivity, "User not found", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun createFriendForUser(
        userId: String,
        displayName: String,
        email: String,
        sent: Boolean = false,
        accepted: Boolean = false
    ) {
        val newFriend = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "sent" to sent,
            "accepted" to accepted
        )

        db.collection("users").document(userId).collection("friends").add(newFriend)
    }


    private fun changeFriendStatus(
        userId: String,
        eventId: String,
        displayName: String,
        email: String,
        sent: Boolean,
        accepted: Boolean
    ) {
        val changedFriendStatus = hashMapOf(
            "accepted" to accepted,
            "displayName" to displayName,
            "sent" to sent,
            "email" to email
        )

        db.collection("users").document(userId).collection("friends").document(eventId)
            .set(changedFriendStatus)
    }


    fun addFriendForUser(name: String) {
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            createFriendForUser(
                                firebaseAuth.currentUser?.uid!!,
                                currentUser.displayName,
                                currentUser.email,
                                sent = true,
                                accepted = false
                            )
                            createFriendForUser(
                                document.id,
                                firebaseAuth.currentUser?.displayName!!,
                                firebaseAuth.currentUser?.email!!,
                                sent = false,
                                accepted = false
                            )
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

    fun acceptFriendRequestHelper(name: String) {
        acceptFriendRequest(name, firebaseAuth.currentUser?.uid!!)
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            acceptFriendRequest(
                                firebaseAuth.currentUser?.displayName!!,
                                document.id
                            )
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

    fun deleteFriendRequestHelper(name: String) {
        deleteFriendRequest(name, firebaseAuth.currentUser?.uid!!)
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            deleteFriendRequest(
                                firebaseAuth.currentUser?.displayName!!,
                                document.id
                            )
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


    private fun acceptFriendRequest(name: String, userId: String) {
        val docRef = db.collection("users").document(userId).collection("friends")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentFriend = document.toObject(Friend::class.java)
                        if (currentFriend.displayName == name) {
                            changeFriendStatus(
                                userId,
                                document.id,
                                currentFriend.displayName,
                                currentFriend.email,
                                currentFriend.sent,
                                true
                            )
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

    private fun deleteFriendRequest(name: String, userId: String) {
        val docRef = db.collection("users").document(userId).collection("friends")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentFriend = document.toObject(Friend::class.java)
                        if (currentFriend.displayName == name) {
                            db.collection("users").document(userId).collection("friends")
                                .document(document.id).delete()
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun deleteFriend(name: String) {
        deleteFriendRequest(name, firebaseAuth.currentUser?.uid!!)
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            deleteFriendRequest(
                                firebaseAuth.currentUser?.displayName!!,
                                document.id
                            )
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


}