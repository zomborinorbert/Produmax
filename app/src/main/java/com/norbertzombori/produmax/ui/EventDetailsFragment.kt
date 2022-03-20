package com.norbertzombori.produmax.ui


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import kotlinx.android.synthetic.main.fragment_event_details.*

class EventDetailsFragment : Fragment(R.layout.fragment_event_details) {
    private val viewModel: CreateEventViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_event_name.text = viewModel.selected.value?.eventName
        tv_event_date.text = viewModel.selected.value?.eventDate.toString()

        btn_delete_event.setOnClickListener {
            viewModel.deleteEvent()

            val action = EventDetailsFragmentDirections.actionEventDetailsFragmentToPlannerFragment()
            findNavController().navigate(action)
        }
    }

}