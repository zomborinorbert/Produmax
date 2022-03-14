package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.norbertzombori.produmax.data.AppRepository
import com.norbertzombori.produmax.data.Event
import java.util.*

class CreateEventViewModel : ViewModel() {
    val appRepository = AppRepository()
    val eventList = MutableLiveData<MutableList<Event>>()
    val selected = MutableLiveData<Event>()

    init {
        eventList.value = ArrayList()
        eventChangeListener()
    }

    fun select(event: Event) {
        selected.value = event
    }

    fun createEvent(userId: String, eventName: String, eventDate: Date) {
        appRepository.createEventForUser(userId, eventName, eventDate)
    }

    fun getUserId() = appRepository.firebaseAuth.currentUser?.uid!!

    fun createEventForOtherUser(name: String, eventName: String, eventDate: Date) {
        appRepository.createEventForUserWithName(name, eventName, eventDate)
    }

    fun acceptInviteForEvent(event: Event) {
        appRepository.acceptInviteForEvent(event)
    }

    private fun eventChangeListener() {
        appRepository.db.collection("users").document(appRepository.firebaseAuth.currentUser?.uid!!)
            .collection("events").orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED && dc.document.toObject(Event::class.java).accepted) {
                        eventList.value?.add(dc.document.toObject(Event::class.java))
                        eventList.value = eventList.value
                    }
                }
            }
    }

}
