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

class TodoRepository {
    val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()


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




}