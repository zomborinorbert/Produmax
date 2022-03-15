package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.norbertzombori.produmax.data.AppRepository
import com.norbertzombori.produmax.data.Habit
import java.util.*

class HabitsViewModel : ViewModel() {
    val appRepository = AppRepository()
    val habitList = MutableLiveData<MutableList<Habit>>()
    val selected = MutableLiveData<Habit>()

    init {
        habitList.value = ArrayList()
        eventChangeListener()
    }

    fun select(user: Habit) {
        selected.value = user
    }

    fun changeDone(position: Int) {
        val currentHabit = habitList.value?.get(position)
        currentHabit?.let {
            it.done = !it.done
            habitList.value?.set(position, it)
        }
    }

    fun checkHabit(position: Int) {
        habitList.value?.get(position)?.let { appRepository.addHabitForToday(it.habitDescription) }
    }

    fun unCheckHabit(position: Int) {
        habitList.value?.get(position)
            ?.let { appRepository.deleteHabitForTodayHelper(it.habitDescription) }
    }

    fun deleteHabit(position: Int) {
        habitList.value?.get(position)?.let {
            appRepository.deleteHabit(it.habitDescription)
        }
        habitList.value?.removeAt(position)
    }

    fun editHabitName(position: Int, newHabitDescription: String){
        habitList.value?.get(position)?.let {
            appRepository.editHabitName(it.habitDescription, newHabitDescription)
            it.habitDescription = newHabitDescription
            habitList.value?.set(position, it)
        }
    }

    private fun eventChangeListener() {
        appRepository.db.collection("users").document(appRepository.firebaseAuth.currentUser?.uid!!)
            .collection("habits")
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        habitList.value?.add(dc.document.toObject(Habit::class.java))
                        habitList.value = habitList.value
                    }
                }
            }
    }

}
