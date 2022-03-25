package com.norbertzombori.produmax.viewmodels


import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.norbertzombori.produmax.data.AppRepository
import com.norbertzombori.produmax.data.Event
import java.text.SimpleDateFormat
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

    fun getUserId() = appRepository.firebaseAuth.currentUser?.uid!!

    fun createEventForUser(name: String, eventName: String, eventDate: Date, newDateEnd: Date, eventLength: Int, members: List<String>, accepted: Boolean) {
        appRepository.createEventForUserWithName(name, eventName, eventDate, newDateEnd, eventLength, members, accepted)
    }

    fun acceptInviteForEvent(event: Event) {
        appRepository.acceptInviteForEvent(event)
    }

    private fun eventChangeListener() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val date = "25-3-2022 9:5"
        val datee = "25-3-2022 11:5"
        val date1 = dateFormat.parse(date)
        val date2 = dateFormat.parse(datee)
        val startDate = Timestamp(date1)
        val endDate = Timestamp(date2)

        Log.d(ContentValues.TAG, "THIS IS THE FUCKING DATE TIMESTAMP IN THE VIEWMODEL ${Timestamp(date1)}")
        Log.d(ContentValues.TAG, "THIS IS THE FUCKING DATE TIMESTAMP IN THE VIEWMODEL ${Timestamp(date2)}")

        appRepository.db.collection("users").document(appRepository.firebaseAuth.currentUser?.uid!!)
            .collection("events")
            .whereGreaterThan("eventDate", startDate)
            .whereLessThan("eventDate", endDate)
            .orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED && dc.document.toObject(Event::class.java).accepted) {
                        eventList.value?.add(dc.document.toObject(Event::class.java))
                        eventList.value = eventList.value
                    }
                }
            }
    }

    fun deleteEvent() {
        appRepository.deleteEvent(selected.value!!.eventName)
        eventList.value?.remove(selected.value)
    }

}
