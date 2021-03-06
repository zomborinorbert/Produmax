package com.norbertzombori.produmax.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.TodoAdapter
import com.norbertzombori.produmax.data.Todo
import com.norbertzombori.produmax.viewmodels.TodoViewModel
import kotlinx.android.synthetic.main.fragment_create_todo.*
import kotlinx.android.synthetic.main.fragment_todos.*
import kotlinx.android.synthetic.main.fragment_tracker.*
import kotlinx.android.synthetic.main.fragment_tracker.recycler_view
import java.util.ArrayList

class
ToDoFragment : Fragment(R.layout.fragment_todos), TodoAdapter.OnItemClickListener {
    private val viewModel: TodoViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoList: MutableList<Todo>
    private lateinit var todoAdapter: TodoAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("To do")
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)

        todoList = mutableListOf()
        todoAdapter = TodoAdapter(viewModel.todoList.value!!, this)
        recyclerView.adapter = todoAdapter

        viewModel.todoList.observe(viewLifecycleOwner) {
            todoAdapter.notifyDataSetChanged()
        }

        btn_create_todo.setOnClickListener {
            val action = ToDoFragmentDirections.actionToDoFragmentToCreateTodoFragment2()
            findNavController().navigate(action)
        }

    }


    override fun onItemClick(position: Int) {
        todoList = viewModel.todoList.value!!
        viewModel.checkTodo(position)
        viewModel.changeDone(position)
        todoAdapter.notifyItemChanged(position)
    }

    override fun onItemLongClick(position: Int) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(viewModel.todoList.value!![position].description)
            .setPositiveButton("Delete Todo") { _, _ ->
                viewModel.deleteTodo(position)
                todoAdapter.notifyDataSetChanged()
            }.setNegativeButton("Rename Todo") { _, _ ->
                showEdit(position)
            }.show()
    }

    private fun showEdit(position: Int) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_text_habit_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.et_editText)

        with(builder) {
            setTitle("Enter the new name!")
            setPositiveButton("OK") { _, _ ->
                if (editText.text.length in 5..29) {
                    viewModel.todoList.value!!.forEach { x ->
                        if (x.description == editText.text.toString()) {
                            Toast.makeText(
                                requireActivity(),
                                "Todo with this name already exists!",
                                Toast.LENGTH_LONG
                            ).show()
                            return@setPositiveButton
                        }
                    }
                    viewModel.editTodoDesc(position, editText.text.toString())
                    todoAdapter.notifyItemChanged(position)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Todo name is too short or too long!(length should be between 5-29 char long)",
                        Toast.LENGTH_LONG
                    ).show()
                    showEdit(position)
                }
            }
            setNegativeButton("Discard") { _, _ ->

            }
            setView(dialogLayout)
            show()
        }
    }
}

