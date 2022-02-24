package com.norbertzombori.produmax.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import kotlinx.android.synthetic.main.fragment_create_event.*
import java.util.*

class CreateEventFragment : DialogFragment(R.layout.fragment_create_event),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var viewModel: CreateEventViewModel

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = CreateEventViewModel()

        pickDate()

        button_create_event.setOnClickListener {
            val newDate = Date(savedYear - 1900, savedMonth, savedDay, savedHour, savedMinute)
            viewModel.createEvent(
                viewModel.getUserId(),
                edit_text_event_name.text.toString(),
                newDate
            )

            val action = CreateEventFragmentDirections.actionCreateEventFragmentToPlannerFragment()
            findNavController().navigate(action)

            if (edit_text_other_user.text.toString().isNotEmpty()) {
                viewModel.createEventForOtherUser(
                    edit_text_other_user.text.toString(),
                    edit_text_event_name.text.toString(),
                    newDate
                )
            }
        }
    }

    private fun getDateTimeCalender() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)

    }

    private fun pickDate() {
        button_date_time_picker.setOnClickListener {
            getDateTimeCalender()

            DatePickerDialog(requireActivity(), this, year, month, day).show()
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        savedYear = year
        savedMonth = month
        savedDay = day

        getDateTimeCalender()
        TimePickerDialog(requireActivity(), this, hour, minute, true).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        savedHour = hour
        savedMinute = minute

        textView_selected_date.text = "$savedYear-$savedMonth-$savedDay $savedHour $savedMinute"
    }


}