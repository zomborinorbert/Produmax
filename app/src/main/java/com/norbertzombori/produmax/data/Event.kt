package com.norbertzombori.produmax.data

import com.google.firebase.Timestamp

data class Event(var eventName: String = "", var eventDate: Timestamp = Timestamp.now(), var accepted: Boolean = false, var members: List<String> = listOf())