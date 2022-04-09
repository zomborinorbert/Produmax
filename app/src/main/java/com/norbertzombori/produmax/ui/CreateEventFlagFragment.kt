package com.norbertzombori.produmax.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.CreateEventFlagViewModel
import com.norbertzombori.produmax.viewmodels.CreateHabitViewModel
import kotlinx.android.synthetic.main.fragment_create_event.*
import kotlinx.android.synthetic.main.fragment_create_event_flag.*
import kotlinx.android.synthetic.main.fragment_create_habit.*

class CreateEventFlagFragment : Fragment(R.layout.fragment_create_event_flag), AdapterView.OnItemSelectedListener {
    private val viewModel: CreateEventFlagViewModel by viewModels()
    private var flagImportance = "LOW"
    private var flagColor = "BLACK"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().setTitle("Flag creation")
        super.onViewCreated(view, savedInstanceState)

        val importanceItems = arrayOf("LOW", "MEDIUM", "HIGH")
        val importanceAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, importanceItems)
        create_flag_importance_spinner.adapter = importanceAdapter
        create_flag_importance_spinner.onItemSelectedListener = this

        val colorItems = arrayOf("BLACK", "RED", "PURPLE")
        val colorAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, colorItems)
        create_flag_colors_spinner.adapter = colorAdapter
        create_flag_colors_spinner.onItemSelectedListener = this

        btn_add_event_flag.setOnClickListener {
            Log.d("form","ASDSADASDSA $flagImportance")
            Log.d("form","ASDSADASDSA $flagColor")
            viewModel.createNewFlag(flagImportance, flagColor, et_flag_name.text.toString())

            val action = CreateEventFlagFragmentDirections.actionCreateEventFlagFragmentToPlannerFragment()
            findNavController().navigate(action)
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when(p0!!.id){
            R.id.create_flag_importance_spinner -> flagImportance = p0?.getItemAtPosition(p2) as String
            R.id.create_flag_colors_spinner -> flagColor = p0?.getItemAtPosition(p2) as String
        }
        Log.d("form","ASDSADASDSA $flagImportance")
        Log.d("form","ASDSADASDSA $flagColor")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}

