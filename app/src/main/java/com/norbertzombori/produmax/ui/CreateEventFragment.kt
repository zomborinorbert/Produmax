package com.norbertzombori.produmax.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.FriendsAdapter
import com.norbertzombori.produmax.data.EventFlag
import com.norbertzombori.produmax.data.Friend
import com.norbertzombori.produmax.viewmodels.PlannerViewModel
import com.norbertzombori.produmax.viewmodels.FriendsViewModel
import kotlinx.android.synthetic.main.fragment_create_event.*
import java.util.*


class CreateEventFragment : DialogFragment(R.layout.fragment_create_event),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
    FriendsAdapter.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private val viewModel: PlannerViewModel by viewModels()
    private val friendsViewModel: FriendsViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: MutableList<Friend>
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var invitationList: MutableList<Friend>
    private lateinit var eventFlagList: MutableList<EventFlag>
    private lateinit var importanceAdapter: ArrayAdapter<String>
    private lateinit var colorAdapter: ArrayAdapter<String>

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

    private var eventLength = 1
    private var eventImportance = "LOW"
    private var eventColor = "BLACK"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().setTitle("Event creation")

        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)

        userList = mutableListOf()
        friendsAdapter = FriendsAdapter(friendsViewModel.userList.value!!, this)
        recyclerView.adapter = friendsAdapter

        friendsViewModel.userList.observe(viewLifecycleOwner) {
            friendsAdapter.notifyDataSetChanged()
        }

        pickDate()

        invitationList = ArrayList()

        btn_create_event.setOnClickListener {
            val membersList = ArrayList<String>()
            membersList.add(viewModel.appRepository.firebaseAuth.currentUser!!.displayName!!)

            for (friend in invitationList) {
                membersList.add(friend.displayName)
            }

            val newDate = Date(savedYear - 1900, savedMonth, savedDay, savedHour, savedMinute)
            val newDateEnd = Date(savedYear - 1900, savedMonth, savedDay, savedHour + eventLength, savedMinute)


            viewModel.createEventForUser(
                viewModel.appRepository.firebaseAuth.currentUser!!.displayName!!,
                et_event_name.text.toString(),
                newDate,
                newDateEnd,
                eventLength,
                eventImportance,
                eventColor,
                membersList,
                true
            )

            for (member in membersList.drop(1)) {
                viewModel.createEventForUser(
                    member,
                    et_event_name.text.toString(),
                    newDate,
                    newDateEnd,
                    eventLength,
                    eventImportance,
                    eventColor,
                    membersList,
                    false
                )
            }

            scheduleNotification()

            val action = CreateEventFragmentDirections.actionCreateEventFragmentToPlannerFragment()
            findNavController().navigate(action)
        }

        createLocalNotifications()

        eventFlagList = ArrayList()
        var flagList : MutableList<String>
        flagList = ArrayList()
        val flagAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, flagList)
        flags_spinner.adapter = flagAdapter
        flags_spinner.onItemSelectedListener = this

        val eventLengthItems = arrayOf(1, 2, 3, 4, 5, 6, 8, 9, 10 , 11, 12)
        val eventLengthAdapter: ArrayAdapter<Int> =
            ArrayAdapter<Int>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, eventLengthItems)
        planets_spinner.adapter = eventLengthAdapter
        planets_spinner.onItemSelectedListener = this

        val importanceItems = arrayOf("LOW", "MEDIUM", "HIGH")
        importanceAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, importanceItems)
        importance_spinner.adapter = importanceAdapter
        importance_spinner.onItemSelectedListener = this

        val colorItems = arrayOf("BLACK", "RED", "PURPLE")
        colorAdapter =
            ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, colorItems)
        colors_spinner.adapter = colorAdapter
        colors_spinner.onItemSelectedListener = this

        viewModel.getFlagList()

        viewModel.flagList.observe(viewLifecycleOwner){
            it?.forEach{
                if(!flagList.contains(it.flagName)){
                    flagList.add(it.flagName)
                    eventFlagList.add(it)
                    flagAdapter.notifyDataSetChanged()
                }
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
        invitationList = arrayListOf()

        textView_selected_date.text = "$savedYear-$savedMonth-$savedDay $savedHour $savedMinute"
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when(p0!!.id){
            R.id.planets_spinner -> eventLength = p0?.getItemAtPosition(p2) as Int
            R.id.importance_spinner -> eventImportance = p0?.getItemAtPosition(p2) as String
            R.id.colors_spinner -> eventColor = p0?.getItemAtPosition(p2) as String
            R.id.flags_spinner -> {
                Log.d("form","DSADdsadasSADSADAS $eventLength")
                val currentFlag = getEventFlagByFlagName(p0?.getItemAtPosition(p2) as String)

                eventColor = currentFlag!!.flagColor
                val colorSpinnerPosition: Int = colorAdapter.getPosition(eventColor)
                colors_spinner.setSelection(colorSpinnerPosition)

                eventImportance = currentFlag!!.flagImportance
                val importanceSpinnerPosition: Int = importanceAdapter.getPosition(eventImportance)
                importance_spinner.setSelection(importanceSpinnerPosition)
            }
        }
    }

    fun getEventFlagByFlagName(flagName: String) : EventFlag? {
        eventFlagList.forEach {
            if(it.flagName == flagName){
                return it
            }
        }
        return null
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun scheduleNotification() {
        val intent = Intent(activity?.applicationContext, NotificationCreate::class.java)
        intent.putExtra(messageExtra, et_event_name.text.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            activity?.applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val time = getTime()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun getTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(savedYear, savedMonth, savedDay, savedHour, savedMinute)
        return calendar.timeInMillis
    }

    private fun createLocalNotifications() {
        val name = "This is the name of is"
        val desc = "This is the description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onItemClick(position: Int) {
        userList = friendsViewModel.userList.value!!
        if (!invitationList.contains(userList[position])) {
            invitationList.add(userList[position])
            Toast.makeText(
                requireActivity(),
                "User ${userList[position].displayName} added",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            invitationList.remove(userList[position])
            Toast.makeText(
                requireActivity(),
                "User ${userList[position].displayName} removed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}