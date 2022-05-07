package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.norbertzombori.produmax.data.TodoRepository
import com.norbertzombori.produmax.data.Todo
import java.util.*

class TodoViewModel : ViewModel() {
    val todoRepository = TodoRepository()
    val todoList = MutableLiveData<MutableList<Todo>>()
    val selected = MutableLiveData<Todo>()

    init {
        todoList.value = ArrayList()
        eventChangeListener()
    }

    fun select(todo: Todo) {
        selected.value = todo
    }

    fun changeDone(position: Int) {
        val currentTodo = todoList.value?.get(position)
        currentTodo?.let {
            it.done = !it.done
            todoList.value?.set(position, it)
        }
    }

    fun checkTodo(position: Int) {
        todoList.value?.get(position)?.let { todoRepository.checkTodoForUser(it.description) }
    }

    fun createNewTodo(description: String, members: List<String>) {
        todoRepository.createTodoForUsers(description, members)
    }

    fun deleteTodo(position: Int) {
        todoList.value?.get(position)?.let {
            todoRepository.deleteTodoForUser(it.description)
        }
        todoList.value?.removeAt(position)
    }

    fun editTodoDesc(position: Int, newHabitDescription: String) {
        todoList.value?.get(position)?.let {
            todoRepository.editTodoDesc(it.description, newHabitDescription)
            it.description = newHabitDescription
            todoList.value?.set(position, it)
        }
    }

    private fun eventChangeListener() {
        todoRepository.db.collection("users")
            .document(todoRepository.firebaseAuth.currentUser?.uid!!)
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
