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
import com.norbertzombori.produmax.adapters.HabitsAdapter
import com.norbertzombori.produmax.data.Habit
import com.norbertzombori.produmax.viewmodels.HabitsViewModel
import kotlinx.android.synthetic.main.fragment_landing.*
import kotlinx.android.synthetic.main.fragment_tracker.*
import kotlinx.android.synthetic.main.fragment_tracker.recycler_view

class TrackerFragment : Fragment(R.layout.fragment_tracker), HabitsAdapter.OnItemClickListener {
    private val viewModel: HabitsViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var habitList: MutableList<Habit>
    private lateinit var habitsAdapter: HabitsAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)

        habitList = mutableListOf()
        habitsAdapter = HabitsAdapter(viewModel.habitList.value!!, this)
        recyclerView.adapter = habitsAdapter

        viewModel.habitList.observe(viewLifecycleOwner) {
            habitsAdapter.notifyDataSetChanged()
            Log.d(ContentValues.TAG, "New document added")
        }

        btn_edit_habits.setOnClickListener {
            val action = TrackerFragmentDirections.actionTrackerFragmentToEditHabitsFragment()
            findNavController().navigate(action)
        }

    }

    override fun onItemClick(position: Int) {
        habitList = viewModel.habitList.value!!
        if(!habitList[position].done){
            viewModel.checkHabit(position)
        }else{
            viewModel.unCheckHabit(position)
        }
        viewModel.changeDone(position)
        habitsAdapter.notifyItemChanged(position)
    }


}