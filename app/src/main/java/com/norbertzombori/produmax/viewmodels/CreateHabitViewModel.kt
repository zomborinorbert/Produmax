package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.AppRepository

class CreateHabitViewModel : ViewModel() {
    val appRepository = AppRepository()

    fun createNewHabit(description: String){
        appRepository.createHabitForUser(description)
    }
}
