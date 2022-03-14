package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.AppRepository

class HomeViewModel : ViewModel() {
    val appRepository = AppRepository()

    fun checkForNewEvent(){
        appRepository.checkForNewEvent()
    }
}