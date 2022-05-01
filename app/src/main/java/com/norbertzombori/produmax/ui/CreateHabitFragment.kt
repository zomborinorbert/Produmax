package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.HabitsViewModel
import kotlinx.android.synthetic.main.fragment_create_habit.*

class CreateHabitFragment : Fragment(R.layout.fragment_create_habit) {
    private val viewModel: HabitsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().setTitle("Habit creation")
        super.onViewCreated(view, savedInstanceState)

        btn_add_habit.setOnClickListener {
            when (et_habit_desc.text.length) {
                in 5..29 -> {
                    viewModel.createNewHabit(et_habit_desc.text.toString())
                }
                else -> {
                    Toast.makeText(
                        requireActivity(),
                        "Habit name too short or too long!(length should be between 5-29 char long)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            val action = CreateHabitFragmentDirections.actionCreateHabitFragmentToTrackerFragment()
            findNavController().navigate(action)
        }
    }
}

