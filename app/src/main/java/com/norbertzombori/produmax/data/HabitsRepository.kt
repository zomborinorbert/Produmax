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

class HabitsRepository {
    val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()
    val statList = MutableLiveData<MutableList<HabitStatistics>>()


    fun createHabitForUser(description: String) {
        val newHabit = hashMapOf(
            "habitDescription" to description,
            "done" to false
        )

        db.collection("users").document(firebaseAuth.currentUser?.uid!!).collection("habits")
            .add(newHabit)
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
                            db.collection("users").document(firebaseAuth.currentUser?.uid!!)
                                .collection("habits")
                                .document(document.id).delete()
                            break
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

                            db.collection("users").document(firebaseAuth.currentUser?.uid!!)
                                .collection("habits")
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

                    statList.value?.forEach { duplicateList.add(it.copy()) }
                    statList.value = duplicateList
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


}