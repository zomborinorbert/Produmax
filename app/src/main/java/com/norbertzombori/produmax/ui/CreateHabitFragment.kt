package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.CreateHabitViewModel
import kotlinx.android.synthetic.main.fragment_create_habit.*

class CreateHabitFragment : Fragment(R.layout.fragment_create_habit) {
    private val viewModel: CreateHabitViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().setTitle("Habit creation")
        super.onViewCreated(view, savedInstanceState)

        btn_add_habit.setOnClickListener {
            if(et_habit_desc.text.toString().isNotEmpty()){
                viewModel.createNewHabit(et_habit_desc.text.toString())
            }

            val action = CreateHabitFragmentDirections.actionCreateHabitFragmentToTrackerFragment()
            findNavController().navigate(action)
        }
    }
}

