package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.LoginRegisterRepository

class HomeViewModel : ViewModel() {
    val loginRegisterRepository = LoginRegisterRepository()

    fun checkForNewEvent() {
        loginRegisterRepository.checkForNewEvent()
    }

    fun checkNewDayForTracker() {
        loginRegisterRepository.saveLoginDay()
    }

    fun disableNewEvent() {
        loginRegisterRepository.disableNewEvent()
    }
}