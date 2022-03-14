package com.norbertzombori.produmax.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.norbertzombori.produmax.data.AppRepository
import com.norbertzombori.produmax.data.Friend
import java.util.*

class FriendsViewModel : ViewModel() {
    val appRepository = AppRepository()
    val userList = MutableLiveData<MutableList<Friend>>()
    val selected = MutableLiveData<Friend>()

    init {
        userList.value = ArrayList()
        eventChangeListener()
    }

    fun select(user: Friend) {
        selected.value = user
    }

    fun addFriend(userName: String) {
        appRepository.addFriendForUser(userName)
    }

    fun acceptFriendRequest(name: String, position: Int) {
        appRepository.acceptFriendRequestHelper(name)
        val currentFriend = userList.value?.get(position)
        currentFriend?.let {
            it.accepted = true
            userList.value?.set(position, it)
        }
    }

    private fun eventChangeListener() {
        appRepository.db.collection("users").document(appRepository.firebaseAuth.currentUser?.uid!!)
            .collection("friends")
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        userList.value?.add(dc.document.toObject(Friend::class.java))
                        userList.value = userList.value
                    }

                }
            }
    }

}
