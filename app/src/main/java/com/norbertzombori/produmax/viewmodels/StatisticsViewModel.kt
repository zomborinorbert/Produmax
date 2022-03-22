package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.norbertzombori.produmax.data.AppRepository
import com.norbertzombori.produmax.data.Event
import java.util.*

class StatisticsViewModel : ViewModel() {
    val appRepository = AppRepository()
}
