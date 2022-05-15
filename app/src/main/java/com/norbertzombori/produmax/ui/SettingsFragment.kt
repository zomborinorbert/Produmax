package com.norbertzombori.produmax.ui


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.SettingsViewModel
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings), AdapterView.OnItemSelectedListener {
    private val viewModel: SettingsViewModel by viewModels()
    private var check: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getProfileVisibility()
        requireActivity().setTitle("Settings")

        viewModel.visibilityLiveData.observe(viewLifecycleOwner) {
            if (it) profile_visibility_spinner.setSelection(1) else profile_visibility_spinner.setSelection(
                0
            )
        }

        val eventLengthItems = arrayOf("Private", "Public")
        val eventLengthAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                eventLengthItems
            )
        profile_visibility_spinner.adapter = eventLengthAdapter
        profile_visibility_spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (check > 0) {
            val setting = p0?.getItemAtPosition(p2) as String
            if (setting == "Private") viewModel.editProfileVisibility(false) else viewModel.editProfileVisibility(
                true
            )
        }
        check++
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}