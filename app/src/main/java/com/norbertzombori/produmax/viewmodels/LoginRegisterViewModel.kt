package com.norbertzombori.produmax.viewmodels

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.LoginRegisterRepository

class LoginRegisterViewModel : ViewModel() {
    val loginRegisterRepository = LoginRegisterRepository()
    val userMutableLiveData = loginRegisterRepository.userMutableLiveData

    fun login(email: String, password: String, mainActivity: FragmentActivity){
        loginRegisterRepository.login(email, password, mainActivity)
    }

    fun register(email: String, password: String, username: String, mainActivity: FragmentActivity){
        loginRegisterRepository.registerCheck(email, password, username, mainActivity)
    }

    fun resetPassword(email: String, mainActivity: FragmentActivity){
        loginRegisterRepository.resetPassword(email, mainActivity)
    }
}