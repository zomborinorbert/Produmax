package com.norbertzombori.produmax.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.FriendsAdapter
import com.norbertzombori.produmax.data.Friend
import com.norbertzombori.produmax.viewmodels.CreateTodoViewModel
import com.norbertzombori.produmax.viewmodels.FriendsViewModel
import kotlinx.android.synthetic.main.fragment_create_event.*
import kotlinx.android.synthetic.main.fragment_create_event.recycler_view
import kotlinx.android.synthetic.main.fragment_create_todo.*
import java.util.*

class CreateTodoFragment : Fragment(R.layout.fragment_create_todo),
    FriendsAdapter.OnItemClickListener {
    private val viewModel: CreateTodoViewModel by viewModels()
    private val friendsViewModel: FriendsViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: MutableList<Friend>
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var invitationList: MutableList<Friend>
    private lateinit var membersList: ArrayList<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Todo creation")

        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)

        userList = mutableListOf()
        friendsAdapter = FriendsAdapter(friendsViewModel.userList.value!!, this)
        recyclerView.adapter = friendsAdapter

        friendsViewModel.userList.observe(viewLifecycleOwner) {

            friendsAdapter.notifyDataSetChanged()
            Log.d(ContentValues.TAG, "New document added")
        }

        invitationList = ArrayList()

        btn_add_todo.setOnClickListener {
            membersList = ArrayList()
            membersList.add(viewModel.appRepository.firebaseAuth.uid!!)
            for (friend in invitationList) {
                membersList.add(friend.displayName)
            }
            viewModel.createNewTodo(et_todo_desc.text.toString(), membersList)
            val action = CreateTodoFragmentDirections.actionCreateTodoFragment2ToToDoFragment()
            findNavController().navigate(action)
        }
    }

    override fun onItemClick(position: Int) {
        userList = friendsViewModel.userList.value!!

        if (invitationList.contains(userList[position])) {
            invitationList.remove(userList[position])
            Toast.makeText(
                requireActivity(),
                "User ${userList[position].displayName} removed",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireActivity(),
                "User ${userList[position].displayName} added",
                Toast.LENGTH_SHORT
            ).show()
            invitationList.add(userList[position])
        }
    }

}