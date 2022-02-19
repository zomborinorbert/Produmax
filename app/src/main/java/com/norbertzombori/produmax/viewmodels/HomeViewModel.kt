package com.norbertzombori.produmax.viewmodels

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.AppRepository
import com.norbertzombori.produmax.data.Event

class HomeViewModel() : ViewModel() {
    val appRepository = AppRepository()

    fun createEvent(userId: String, event: Event){
        appRepository.createEventForUser(userId, event)
    }
}