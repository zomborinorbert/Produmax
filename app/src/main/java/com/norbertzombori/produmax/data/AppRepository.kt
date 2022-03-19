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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AppRepository {
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

    private fun createUserCollection(userId: String, email: String, displayName: String) {
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

    fun createHabitForUser(description: String) {
        val newHabit = hashMapOf(
            "habitDescription" to description,
            "done" to false
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits").add(newHabit)
    }

    private fun createFriendForUser(userId: String, displayName: String, email: String, sent: Boolean = false, accepted: Boolean = false) {
        val newFriend = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "sent" to sent,
            "accepted" to accepted
        )

        db.collection("users").document(userId).collection("friends").add(newFriend)
    }


    private fun changeFriendStatus(userId: String, eventId: String, displayName: String, email: String, sent: Boolean, accepted: Boolean) {
        val changedFriendStatus = hashMapOf(
            "accepted" to accepted,
            "displayName" to displayName,
            "sent" to sent,
            "email" to email
        )

        db.collection("users").document(userId).collection("friends").document(eventId).set(changedFriendStatus)
    }

    private fun changeInviteStatusForEvent(event: Event, eventId: String){
        val changedEvent = hashMapOf(
            "eventName" to event.eventName,
            "eventDate" to event.eventDate,
            "accepted" to !event.accepted
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events").document(eventId).set(changedEvent)
    }

    fun createEventForUserWithName(name: String, eventName: String, eventDate: Date) {
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
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

    fun addHabitForToday(habitDescription: String) {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentHabit = document.toObject(Habit::class.java)
                        Log.d(TAG, "ASDASDASDASDASD")
                        if (currentHabit.habitDescription == habitDescription) {
                            Log.d(TAG, "ASDASDASDASDASD")
                            checkHabitForToday(document.id, habitDescription)
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

    private fun checkHabitForToday(habitId: String, habitDescription: String) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)

        val newDate = hashMapOf(
            "date" to formatted
        )

        val newHabitCheck = hashMapOf(
            "done" to true,
            "habitDescription" to habitDescription
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits").document(habitId).set(newHabitCheck)
        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits").document(habitId).collection("datesDone").add(newDate)
    }

    fun deleteHabitForTodayHelper(habitDescription: String) {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentHabit = document.toObject(Habit::class.java)
                        if (currentHabit.habitDescription == habitDescription) {
                            deleteHabitForToday(document.id, habitDescription)
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

    private fun deleteHabitForToday(habitId: String, habitDescription: String) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)

        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits").document(habitId).collection("datesDone")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentDate = document.toObject(DateString::class.java)
                        if (currentDate.date == formatted) {
                            unCheckHabitForToday(habitId, document.id, habitDescription)
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

    private fun unCheckHabitForToday(habitId: String, dateId: String, habitDescription: String) {
        val newHabitCheck = hashMapOf(
            "done" to false,
            "habitDescription" to habitDescription
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits").document(habitId).set(newHabitCheck)
        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits").document(habitId).collection("datesDone").document(dateId).delete()
    }

    fun acceptInviteForEvent(event: Event){
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events")
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

    fun checkForNewEvent() {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("events")
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

    fun addFriendForUser(name: String){
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            createFriendForUser(firebaseAuth.currentUser?.uid!!, currentUser.displayName, currentUser.email,
                                sent = true,
                                accepted = false
                            )
                            createFriendForUser(document.id, firebaseAuth.currentUser?.displayName!!, firebaseAuth.currentUser?.email!!,
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

    fun acceptFriendRequestHelper(name: String){
        acceptFriendRequest(name, firebaseAuth.currentUser?.uid!!)
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
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

    private fun acceptFriendRequest(name: String, userId: String){
        val docRef = db.collection("users").document(userId).collection("friends")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentFriend = document.toObject(Friend::class.java)
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

    fun deleteHabit(habitDescription: String) {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentHabit = document.toObject(Habit::class.java)
                        if (currentHabit.habitDescription == habitDescription) {
                            deleteHabitHelper(document.id)
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

    private fun deleteHabitHelper(habitId: String) {
        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits").document(habitId).delete()
    }

    fun editHabitName(habitDescription: String, newHabitDescription: String) {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentHabit = document.toObject(Habit::class.java)
                        if (currentHabit.habitDescription == habitDescription) {
                            editHabitNameHelper(document.id, newHabitDescription, currentHabit.done)
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

    private fun editHabitNameHelper(habitId: String, newHabitDescription: String, done: Boolean) {
        val newHabit = hashMapOf(
            "habitDescription" to newHabitDescription,
            "done" to done
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits").document(habitId).set(newHabit)
    }

    fun createTodoForUsers(description: String, members: List<String>) {
        addTodoForUser(firebaseAuth.currentUser?.uid!!, description, members)
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (members.contains(currentUser.displayName)) {
                            addTodoForUser(document.id, description, members)
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



    fun addTodoForUser(userId: String, description: String, members: List<String>){
        val newTodo = hashMapOf(
            "description" to description,
            "done" to false,
            "members" to members
        )

        db.collection("users").document(userId).collection("todos").add(newTodo)
    }

    fun checkTodoForUser(description: String) {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("todos")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentTodo = document.toObject(Todo::class.java)
                        if (currentTodo.description == description) {
                            checkTodoForUserQuery(document.id, currentTodo.description, currentTodo.members, currentTodo.done)
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

    private fun checkTodoForUserQuery(todoId: String, description: String, members: List<String>, done: Boolean) {
        val doneTodo = hashMapOf(
            "description" to description,
            "done" to !done,
            "members" to members
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("todos").document(todoId).set(doneTodo)
    }

    fun deleteTodoForUser(description: String) {
        val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("todos")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentTodo = document.toObject(Todo::class.java)
                        if (currentTodo.description == description) {
                            deleteTodoForUserQuery(document.id)
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

    private fun deleteTodoForUserQuery(todoId: String) {
        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("todos").document(todoId).delete()
    }


}