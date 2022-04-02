package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.AppRepository

class CreateEventFlagViewModel : ViewModel() {
    val appRepository = AppRepository()

    fun createNewFlag(flagImportance: String, flagColor: String, flagName: String){
        appRepository.createEventFlagForUser(flagImportance, flagColor, flagName)
    }
}
