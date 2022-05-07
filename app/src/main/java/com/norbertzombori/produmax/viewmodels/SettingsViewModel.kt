package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.LoginRegisterRepository

class SettingsViewModel : ViewModel() {
    val loginRegisterRepository = LoginRegisterRepository()
    val visibilityLiveData = loginRegisterRepository.visibilityLiveData

    fun editProfileVisibility(setting: Boolean){
        loginRegisterRepository.editProfileVisibility(setting)
    }

    fun getProfileVisibility(){
        loginRegisterRepository.getProfileVisibility()
    }
}
