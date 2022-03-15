package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.norbertzombori.produmax.data.AppRepository
import com.norbertzombori.produmax.data.Todo
import java.util.*

class TodoViewModel : ViewModel() {
    val appRepository = AppRepository()
    val todoList = MutableLiveData<MutableList<Todo>>()
    val selected = MutableLiveData<Todo>()

    init {
        todoList.value = ArrayList()
        eventChangeListener()
    }

    fun select(todo: Todo) {
        selected.value = todo
    }


    private fun eventChangeListener() {
        appRepository.db.collection("users").document(appRepository.firebaseAuth.currentUser?.uid!!)
            .collection("todos")
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        todoList.value?.add(dc.document.toObject(Todo::class.java))
                        todoList.value = todoList.value
                    }
                }
            }
    }

}
