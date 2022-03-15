package com.norbertzombori.produmax.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.TodoAdapter
import com.norbertzombori.produmax.data.Todo
import com.norbertzombori.produmax.viewmodels.TodoViewModel
import kotlinx.android.synthetic.main.fragment_todos.*
import kotlinx.android.synthetic.main.fragment_tracker.*
import kotlinx.android.synthetic.main.fragment_tracker.recycler_view

class ToDoFragment : Fragment(R.layout.fragment_todos), TodoAdapter.OnItemClickListener {
    private val viewModel: TodoViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoList: MutableList<Todo>
    private lateinit var todoAdapter: TodoAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)

        todoList = mutableListOf()
        todoAdapter = TodoAdapter(viewModel.todoList.value!!, this)
        recyclerView.adapter = todoAdapter

        viewModel.todoList.observe(viewLifecycleOwner) {
            todoAdapter.notifyDataSetChanged()
            Log.d(ContentValues.TAG, "New document added")
        }

        btn_create_todo.setOnClickListener {
            val action = ToDoFragmentDirections.actionToDoFragmentToCreateTodoFragment2()
            findNavController().navigate(action)
        }

    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}

