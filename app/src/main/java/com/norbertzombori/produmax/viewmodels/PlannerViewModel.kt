package com.norbertzombori.produmax.viewmodels


import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.norbertzombori.produmax.data.*
import java.text.SimpleDateFormat
import java.util.*

class PlannerViewModel : ViewModel() {
    val plannerRepository = PlannerRepository()
    val eventList = MutableLiveData<MutableList<Event>>()
    val selected = MutableLiveData<Event>()
    val selectedDay = MutableLiveData<String>()
    val flagList = MutableLiveData<MutableList<EventFlag>>()
    val currentMonth = MutableLiveData<Int>()

    init {
        eventList.value = ArrayList()
        flagList.value = ArrayList()
        currentMonth.value = 0
        eventChangeListener()
    }

    fun getFlagList() {
        val docRef = plannerRepository.db.collection("users").document(plannerRepository.firebaseAuth.currentUser?.uid!!).collection("flags")
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        flagList.value?.add(document.toObject(EventFlag::class.java))
                        flagList.value = flagList.value
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
    }

    fun createNewFlag(flagImportance: String, flagColor: String, flagName: String){
        plannerRepository.createEventFlagForUser(flagImportance, flagColor, flagName)
    }

    fun select(event: Event) {
        selected.value = event
    }

    fun selectDay(day: String){
        selectedDay.value = day
    }

    fun setCurrentMonth(month: Int){
        currentMonth.value = (currentMonth.value?.plus(month))
    }


    fun createEventForUser(
        name: String,
        eventName: String,
        eventDate: Date,
        newDateEnd: Date,
        eventLength: Int,
        eventImportance: String,
        eventColor: String,
        members: List<String>,
        accepted: Boolean
    ) {
        plannerRepository.createEventForUserWithName(
            name,
            eventName,
            eventDate,
            newDateEnd,
            eventLength,
            eventImportance,
            eventColor,
            members,
            accepted
        )
    }

    fun acceptInviteForEvent(event: Event) {
        plannerRepository.acceptInviteForEvent(event)
    }

    fun eventChangeListener(sdate: String = "1-4-2022 9:5", edate: String = "28-4-2022 11:5") {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val date1 = dateFormat.parse(sdate)
        val date2 = dateFormat.parse(edate)
        val startDate = Timestamp(date1)
        val endDate = Timestamp(date2)

        eventList.value = ArrayList()
        eventList.value = eventList.value

        plannerRepository.db.collection("users").document(plannerRepository.firebaseAuth.currentUser?.uid!!)
            .collection("events")
            .whereGreaterThan("eventDate",startDate)
            .whereLessThan("eventDate",endDate)
            .orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED && dc.document.toObject(Event::class.java).accepted) {
                        eventList.value?.add(dc.document.toObject(Event::class.java))
                        eventList.value = eventList.value
                    }
                }
            }

        eventList.value = eventList.value
    }

    fun deleteEvent() {
        selected.value?.let { plannerRepository.deleteEvent(it) }
        eventList.value?.remove(selected.value)
    }

}
