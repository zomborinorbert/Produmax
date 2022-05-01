package com.norbertzombori.produmax.data

import com.google.firebase.Timestamp

data class Event(var eventName: String = "", var eventDate: Timestamp = Timestamp.now(), var eventImportance: String = "", var eventColor: String = "", var eventDateEnd: Timestamp = Timestamp.now(), var eventLength: Int = 1, var accepted: Boolean = false, var members: List<String> = listOf())