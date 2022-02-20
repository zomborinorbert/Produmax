package com.norbertzombori.produmax.data

import com.google.firebase.Timestamp

data class Event(var eventName: String = "", var eventDate: Timestamp = Timestamp.now())