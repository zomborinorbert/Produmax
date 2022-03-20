package com.norbertzombori.produmax.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.FriendsAdapter
import com.norbertzombori.produmax.data.Friend
import com.norbertzombori.produmax.viewmodels.FriendsViewModel
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.android.synthetic.main.fragment_friends.recycler_view

class FriendsFragment : Fragment(R.layout.fragment_friends), FriendsAdapter.OnItemClickListener {
    private val viewModel: FriendsViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: MutableList<Friend>
    private lateinit var friendsAdapter: FriendsAdapter


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)

        userList = mutableListOf()
        friendsAdapter = FriendsAdapter(viewModel.userList.value!!, this)
        recyclerView.adapter = friendsAdapter

        viewModel.userList.observe(viewLifecycleOwner) {
            friendsAdapter.notifyDataSetChanged()
            Log.d(ContentValues.TAG, "New document added")
        }

        btn_send_friend_request.setOnClickListener {
            viewModel.addFriend(et_enter_username.text.toString())
        }
    }

    override fun onItemClick(position: Int) {
        userList = viewModel.userList.value!!
        if (!userList[position].sent && !userList[position].accepted) {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Do you want to accept this friend request?")
                .setNegativeButton("No") { _, _ ->

                }.setPositiveButton("Yes") { _, _ ->
                    viewModel.acceptFriendRequest(userList[position].displayName, position)
                    friendsAdapter.notifyDataSetChanged()
                }.show()
            Toast.makeText(requireActivity(), "Item $position clicked", Toast.LENGTH_SHORT).show()
        }else if(userList[position].accepted){
            viewModel.select(viewModel.userList.value!![position])
            val action = FriendsFragmentDirections.actionFriendsFragmentToProfileFragment()
            findNavController().navigate(action)
        }


    }

}