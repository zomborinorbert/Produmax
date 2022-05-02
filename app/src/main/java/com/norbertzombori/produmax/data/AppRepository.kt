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

class AppRepository {
    val db = Firebase.firestore
    val userMutableLiveData = MutableLiveData<FirebaseUser>()
    val newEventLiveData = MutableLiveData(false)
    val eventChecked = MutableLiveData(false)
    val visibilityLiveData = MutableLiveData(false)
    val firebaseAuth = FirebaseAuth.getInstance()
    val eventList = MutableLiveData<MutableList<HabitStatistics>>()

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

    fun resetPassword(email: String, mainActivity: FragmentActivity){
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
                                    Toast.makeText(mainActivity, "An email has been sent to the given email address!", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            .addOnFailureListener{
                                Log.d(TAG, "get failed with ", it)
                            }
                    } else {
                        Toast.makeText(mainActivity, "There is no user registered with this email address!", Toast.LENGTH_LONG)
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

    private fun createUserCollection(userId: String, email: String, displayName: String) {
        val user = hashMapOf(
            "email" to email,
            "displayName" to displayName,
            "profileVisibility" to false,
            "latestLogin" to LocalDate.now().dayOfMonth
        )

        db.collection("users").document(userId).set(user)
    }

    fun editProfileVisibility(setting: Boolean) {
        val newSetting = hashMapOf(
            "email" to firebaseAuth.currentUser?.email!!,
            "displayName" to firebaseAuth.currentUser?.displayName!!,
            "profileVisibility" to setting
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


    fun disableNewEvent() {
        newEventLiveData.postValue(false)
        eventChecked.postValue(true)
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

    fun addTodoForUser(userId: String, description: String, members: List<String>) {
        val newTodo = hashMapOf(
            "description" to description,
            "done" to false,
            "members" to members
        )

        db.collection("users").document(userId).collection("todos").add(newTodo)
    }

    fun checkTodoForUser(description: String) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("todos")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentTodo = document.toObject(Todo::class.java)
                        if (currentTodo.description == description) {
                            val doneTodo = hashMapOf(
                                "description" to currentTodo.description,
                                "done" to !currentTodo.done,
                                "members" to currentTodo.members
                            )

                            db.collection("users").document(firebaseAuth.currentUser?.uid!!)
                                .collection("todos")
                                .document(document.id).set(doneTodo)
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


    fun deleteTodoForUser(description: String) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("todos")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentTodo = document.toObject(Todo::class.java)
                        if (currentTodo.description == description) {
                            db.collection("users").document(firebaseAuth.currentUser?.uid!!)
                                .collection("todos")
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


    fun editTodoDesc(description: String, newTodoDescription: String) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("todos")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentTodo = document.toObject(Todo::class.java)
                        if (currentTodo.description == description) {
                            val editedTodo = hashMapOf(
                                "description" to newTodoDescription,
                                "done" to currentTodo.done,
                                "members" to currentTodo.members
                            )

                            db.collection("users").document(firebaseAuth.currentUser?.uid!!)
                                .collection("todos")
                                .document(document.id).set(editedTodo)
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


    fun createHabitForUser(description: String) {
        val newHabit = hashMapOf(
            "habitDescription" to description,
            "done" to false
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
            .add(newHabit)
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

    fun addHabitForToday(habitDescription: String) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentHabit = document.toObject(Habit::class.java)
                        if (currentHabit.habitDescription == habitDescription) {
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

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
            .document(habitId).set(newHabitCheck)
        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
            .document(habitId).collection("datesDone").add(newDate)
    }

    fun deleteHabitForTodayHelper(habitDescription: String) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
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

        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
                .document(habitId).collection("datesDone")
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

    private fun unCheckHabit(habitId: String, habitDescription: String) {
        val newHabitCheck = hashMapOf(
            "done" to false,
            "habitDescription" to habitDescription
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
            .document(habitId).set(newHabitCheck)
    }

    private fun unCheckHabitForToday(habitId: String, dateId: String, habitDescription: String) {
        val newHabitCheck = hashMapOf(
            "done" to false,
            "habitDescription" to habitDescription
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
            .document(habitId).set(newHabitCheck)
        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
            .document(habitId).collection("datesDone").document(dateId).delete()
    }

    fun deleteHabit(habitDescription: String) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentHabit = document.toObject(Habit::class.java)
                        if (currentHabit.habitDescription == habitDescription) {
                            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
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

    fun editHabitName(habitDescription: String, newHabitDescription: String) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentHabit = document.toObject(Habit::class.java)
                        if (currentHabit.habitDescription == habitDescription) {
                            val newHabit = hashMapOf(
                                "habitDescription" to newHabitDescription,
                                "done" to currentHabit.done
                            )

                            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
                                .document(document.id).set(newHabit)
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

    fun deleteFriend(name: String){
        deleteFriendRequest(name, firebaseAuth.currentUser?.uid!!)
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        if (currentUser.displayName == name) {
                            deleteFriendRequest(firebaseAuth.currentUser?.displayName!!, document.id)
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




    fun getStatistics() {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val currentHabit = document.toObject(Habit::class.java)
                        getStatisticsDates(document.id, currentHabit.habitDescription)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun getStatisticsDates(habitId: String, habitName: String) {
        val docRef =
            db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
                .document(habitId).collection("datesDone")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    var dateList: ArrayList<String> = ArrayList()
                    for (document in documents) {
                        val currentDate = document.toObject(DateString::class.java)
                        dateList.add(currentDate.date)
                    }
                    val newHabitStatistics = HabitStatistics(habitName, dateList)
                    val duplicateList: MutableList<HabitStatistics> = mutableListOf()

                    duplicateList.add(newHabitStatistics)

                    eventList.value?.forEach { duplicateList.add(it.copy()) }
                    eventList.value = duplicateList
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


}