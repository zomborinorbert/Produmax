package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.AppRepository

class SettingsViewModel : ViewModel() {
    val appRepository = AppRepository()
    val visibilityLiveData = appRepository.visibilityLiveData

    fun editProfileVisibility(setting: Boolean){
        appRepository.editProfileVisibility(setting)
    }

    fun getProfileVisibility(){
        appRepository.getProfileVisibility()
    }
}
