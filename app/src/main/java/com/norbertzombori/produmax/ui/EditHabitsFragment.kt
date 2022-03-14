package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.R
import kotlinx.android.synthetic.main.fragment_edit_habits.*


class EditHabitsFragment : Fragment(R.layout.fragment_edit_habits) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_create_habit.setOnClickListener {
            val action = EditHabitsFragmentDirections.actionEditHabitsFragmentToCreateHabitFragment()
            findNavController().navigate(action)
        }
    }
}