package com.norbertzombori.produmax.viewmodels


import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.norbertzombori.produmax.data.FriendsRepository
import com.norbertzombori.produmax.data.Friend
import java.util.*

class FriendsViewModel : ViewModel() {
    val friendsRepository = FriendsRepository()
    val userList = MutableLiveData<MutableList<Friend>>()
    val selected = MutableLiveData<Friend>()
    val selectedPosition = MutableLiveData<Int>()

    init {
        userList.value = ArrayList()
        eventChangeListener()
    }

    fun select(user: Friend) {
        selected.value = user
    }

    fun selectedPos(position: Int) {
        selectedPosition.value = position
    }

    fun addFriend(userName: String, mainActivity: FragmentActivity) {
        userList.value?.forEach {
            if (it.displayName == userName) {
                Toast.makeText(mainActivity, "User is already your friend!", Toast.LENGTH_LONG)
                    .show()
                return
            }
        }

        friendsRepository.checkIfUserExists(userName, mainActivity)
    }

    fun acceptFriendRequest(name: String, position: Int) {
        friendsRepository.acceptFriendRequestHelper(name)
        val currentFriend = userList.value?.get(position)
        currentFriend?.let {
            it.accepted = true
            userList.value?.set(position, it)
        }
    }

    fun declineFriendRequest(name: String, position: Int) {
        friendsRepository.deleteFriendRequestHelper(name)
        userList.value?.removeAt(position)
    }


    private fun eventChangeListener() {
        friendsRepository.db.collection("users")
            .document(friendsRepository.firebaseAuth.currentUser?.uid!!)
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

    fun deleteFriend(displayName: String, position: Int) {
        friendsRepository.deleteFriend(displayName)
        userList.value?.removeAt(position)
    }

}
