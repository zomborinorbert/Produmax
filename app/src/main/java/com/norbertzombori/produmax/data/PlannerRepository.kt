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

class PlannerRepository {
    val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()

    fun createEventForUser(
        userId: String,
        eventName: String,
        eventDate: Date,
        newDateEnd: Date,
        eventLength: Int,
        eventImportance: String,
        eventColor: String,
        accepted: Boolean = true,
        members: List<String>
    ) {
        val newEvent = hashMapOf(
            "eventName" to eventName,
            "eventDate" to eventDate,
            "eventDateEnd" to newDateEnd,
            "eventLength" to eventLength,
            "eventImportance" to eventImportance,
            "eventColor" to eventColor,
            "accepted" to accepted,
            "members" to members
        )

        db.collection("users").document(userId).collection("events").add(newEvent)
    }


    fun createEventFlagForUser(flagImportance: String, flagColor: String, flagName: String) {
        val newFlag = hashMapOf(
            "flagImportance" to flagImportance,
            "flagColor" to flagColor,
            "flagName" to flagName
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("flags")
            .add(newFlag)
    }

    private fun changeInviteStatusForEvent(event: Event, eventId: String) {
        val changedEvent = hashMapOf(
            "eventName" to event.eventName,
            "eventDate" to event.eventDate,
            "eventDateEnd" to event.eventDateEnd,
            "eventLength" to event.eventLength,
            "eventImportance" to event.eventImportance,
            "eventColor" to event.eventColor,
            "accepted" to !event.accepted,
            "members" to event.members
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events")
            .document(eventId).set(changedEvent)
    }

    fun createEventForUserWithName(
        name: String,
        eventName: String,
        eventDate: Date,
        newDateEnd: Date,
        eventLength: Int,
        eventImportance: String,
        eventColor: String,
        members: List<String>,
        accepted: Boolean
    ) {
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            createEventForUser(
                                document.id,
                                eventName,
                                eventDate,
                                newDateEnd,
                                eventLength,
                                eventImportance,
                                eventColor,
                                accepted,
                                members
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

    fun acceptInviteForEvent(event: Event) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentEvent = document.toObject(Event::class.java)
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


    fun deleteEvent(eventName: String) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentEvent = document.toObject(Event::class.java)
                        if (currentEvent.eventName == eventName) {
                            db.collection("users").document(firebaseAuth.currentUser?.uid!!)
                                .collection("events")
                                .document(document.id).delete()
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


}