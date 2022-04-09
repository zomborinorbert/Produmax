package com.norbertzombori.produmax.ui


import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import kotlinx.android.synthetic.main.fragment_event_details.*
import java.util.ArrayList

class EventDetailsFragment : Fragment(R.layout.fragment_event_details) {
    private val viewModel: CreateEventViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrayAdapter: ArrayAdapter<*>

        var members : MutableList<String>
        members = ArrayList()

        arrayAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, members)
        listview_members.adapter = arrayAdapter

        viewModel.selected.value!!.members.forEach {
            members.add(it)
        }

        arrayAdapter.notifyDataSetChanged()


        tv_event_name.text = viewModel.selected.value?.eventName
        tv_event_date.text = viewModel.selected.value?.eventDate.toString()
        tv_event_importance.text = viewModel.selected.value?.eventImportance

        btn_delete_event.setOnClickListener {
            viewModel.deleteEvent()

            val action = EventDetailsFragmentDirections.actionEventDetailsFragmentToPlannerFragment()
            findNavController().navigate(action)
        }
    }

}