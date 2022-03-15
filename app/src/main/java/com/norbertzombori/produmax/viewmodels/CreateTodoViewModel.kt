package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.AppRepository

class CreateTodoViewModel : ViewModel() {
    val appRepository = AppRepository()

    fun createNewTodo(description: String, members: List<String>){
        appRepository.createTodoForUsers(description, members)
    }
}
