package com.norbertzombori.produmax.viewmodels

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.AppRepository

class LoginRegisterViewModel : ViewModel() {
    val appRepository = AppRepository()
    val userMutableLiveData = appRepository.userMutableLiveData

    fun login(email: String, password: String, mainActivity: FragmentActivity){
        appRepository.login(email, password, mainActivity)
    }

    fun register(email: String, password: String, username: String, mainActivity: FragmentActivity){
        appRepository.registerCheck(email, password, username, mainActivity)
    }

    fun resetPassword(email: String, mainActivity: FragmentActivity){
        appRepository.resetPassword(email, mainActivity)
    }
}