package com.norbertzombori.produmax.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.FriendsAdapter
import com.norbertzombori.produmax.data.Friend
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import com.norbertzombori.produmax.viewmodels.FriendsViewModel
import kotlinx.android.synthetic.main.fragment_create_event.*
import java.util.*

class CreateEventFragment : DialogFragment(R.layout.fragment_create_event),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
    FriendsAdapter.OnItemClickListener {
    private val viewModel: CreateEventViewModel by viewModels()
    private val friendsViewModel: FriendsViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: MutableList<Friend>
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var invitationList: MutableList<Friend>

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)

        userList = mutableListOf()
        friendsAdapter = FriendsAdapter(friendsViewModel.userList.value!!, this)
        recyclerView.adapter = friendsAdapter

        friendsViewModel.userList.observe(viewLifecycleOwner) {
            friendsAdapter.notifyDataSetChanged()
            Log.d(ContentValues.TAG, "New document added")
        }

        pickDate()

        invitationList = ArrayList()

        btn_create_event.setOnClickListener {
            val newDate = Date(savedYear - 1900, savedMonth, savedDay, savedHour, savedMinute)
            viewModel.createEvent(
                viewModel.getUserId(),
                et_event_name.text.toString(),
                newDate
            )

            val action = CreateEventFragmentDirections.actionCreateEventFragmentToPlannerFragment()
            findNavController().navigate(action)

            for (friend in invitationList) {
                viewModel.createEventForOtherUser(
                    friend.displayName,
                    et_event_name.text.toString(),
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
        btn_date_time_picker.setOnClickListener {
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

    override fun onItemClick(position: Int) {
        userList = friendsViewModel.userList.value!!

        if (invitationList.contains(userList[position])) {
            invitationList.remove(userList[position])
            Toast.makeText(
                requireActivity(),
                "User ${userList[position].displayName} removed",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireActivity(),
                "User ${userList[position].displayName} added",
                Toast.LENGTH_SHORT
            ).show()
            invitationList.add(userList[position])
        }
    }

}