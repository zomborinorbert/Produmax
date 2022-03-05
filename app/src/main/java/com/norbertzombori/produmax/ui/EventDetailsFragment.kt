package com.norbertzombori.produmax.ui


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import kotlinx.android.synthetic.main.fragment_event_details.*

class EventDetailsFragment : Fragment(R.layout.fragment_event_details){
    private val viewModel: CreateEventViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textview_event_name.text = viewModel.selected.value?.eventName
        textview_event_date.text = viewModel.selected.value?.eventDate.toString()
    }

}