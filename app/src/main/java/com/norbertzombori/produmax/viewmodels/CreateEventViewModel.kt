package com.norbertzombori.produmax.viewmodels

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.norbertzombori.produmax.data.AppRepository
import com.norbertzombori.produmax.data.Event
import java.time.LocalDateTime
import java.util.*

class CreateEventViewModel() : ViewModel() {
    val appRepository = AppRepository()

    fun createEvent(userId: String, eventName: String, eventDate: Date){
        appRepository.createEventForUser(userId, eventName, eventDate)
    }

    fun getUserId() = appRepository.firebaseAuth.currentUser?.uid!!

    fun createEventForOtherUser(name: String, eventName: String, eventDate: Date){
        appRepository.createEventForUserWithName(name, eventName, eventDate)
    }
}