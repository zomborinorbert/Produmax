package com.norbertzombori.produmax.ui


import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.viewmodels.PlannerViewModel
import kotlinx.android.synthetic.main.fragment_event_details.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EventDetailsFragment : Fragment(R.layout.fragment_event_details) {
    private val viewModel: PlannerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrayAdapter: ArrayAdapter<*>

        var members: MutableList<String>
        members = ArrayList()

        arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1, members
        )
        listview_members.adapter = arrayAdapter

        viewModel.selected.value!!.members.forEach {
            members.add(it)
        }

        arrayAdapter.notifyDataSetChanged()


        val dateFormat: DateFormat = SimpleDateFormat("yyyy MMMM dd hh:mm")
        val strDateStart: String =
            dateFormat.format(Date(viewModel.selected.value?.eventDate!!.seconds * 1000))
        val strDateEnd: String =
            dateFormat.format(Date(viewModel.selected.value?.eventDateEnd!!.seconds * 1000))

        tv_event_name.text = viewModel.selected.value?.eventName
        tv_event_date.text = "$strDateStart-${strDateEnd.split(' ')[3]}"
        tv_event_importance.text = viewModel.selected.value?.eventImportance
        tv_event_color.text = viewModel.selected.value?.eventColor

        btn_delete_event.setOnClickListener {
            viewModel.deleteEvent()

            val action =
                EventDetailsFragmentDirections.actionEventDetailsFragmentToPlannerFragment()
            findNavController().navigate(action)
        }
    }

}