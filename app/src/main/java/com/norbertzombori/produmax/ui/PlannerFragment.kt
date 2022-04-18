package com.norbertzombori.produmax.ui


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.EventAdapter
import com.norbertzombori.produmax.data.Event
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import kotlinx.android.synthetic.main.fragment_planner.*
import kotlinx.android.synthetic.main.fragment_planner.recycler_view
import java.text.SimpleDateFormat
import java.util.*

class PlannerFragment : Fragment(R.layout.fragment_planner), EventAdapter.OnItemClickListener {
    private val viewModel: CreateEventViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: MutableList<Event>
    private lateinit var eventAdapter: EventAdapter
    private var currentWeek: Int = -5


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Planner")
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)


        eventList = ArrayList()
        eventAdapter = EventAdapter(eventList, this)
        recyclerView.adapter = eventAdapter

        eventChangeListener("${getWeekDates()[0]} 00:00", "${getWeekDates()[6]} 23:59")

        btn_navigate_to_create_event.setOnClickListener {
            val action = PlannerFragmentDirections.actionPlannerFragmentToCreateEventFragment()
            findNavController().navigate(action)
        }

        btn_navigate_to_invites.setOnClickListener {
            val action = PlannerFragmentDirections.actionPlannerFragmentToInvitesFragment()
            findNavController().navigate(action)
        }

        btn_navigate_to_create_flag.setOnClickListener {
            val action = PlannerFragmentDirections.actionPlannerFragmentToCreateEventFlagFragment()
            findNavController().navigate(action)
        }

        btn_back_week.setOnClickListener {
            eventList.clear()
            eventAdapter.notifyDataSetChanged()
            currentWeek -= 7
            eventChangeListener("${getWeekDates()[0]} 00:00", "${getWeekDates()[6]} 23:59")
        }

        btn_ahead_week.setOnClickListener {
            eventList.clear()
            eventAdapter.notifyDataSetChanged()
            currentWeek += 7
            eventChangeListener("${getWeekDates()[0]} 00:00", "${getWeekDates()[6]} 23:59")
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireActivity(), "Item $position clicked", Toast.LENGTH_SHORT).show()
        viewModel.select(viewModel.eventList.value!![position])
        Log.d(ContentValues.TAG, "New document added ${viewModel.selected.value?.eventName}")
        val action = PlannerFragmentDirections.actionPlannerFragmentToEventDetailsFragment()
        findNavController().navigate(action)
    }

    fun eventChangeListener(sdate: String = "1-4-2022 9:5", edate: String = "28-4-2022 11:5") {
        Log.d(ContentValues.TAG, "DSAK DSAKÉLDSAÉLKDÉLSAKÉLDSAK $sdate $edate $currentWeek")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val date1 = dateFormat.parse(sdate)
        val date2 = dateFormat.parse(edate)
        val startDate = Timestamp(date1)
        val endDate = Timestamp(date2)

        viewModel.appRepository.db.collection("users").document(viewModel.appRepository.firebaseAuth.currentUser?.uid!!)
            .collection("events")
            .whereGreaterThan("eventDate",startDate)
            .whereLessThan("eventDate",endDate)
            .orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED && dc.document.toObject(Event::class.java).accepted) {
                        eventList.add(dc.document.toObject(Event::class.java))
                        eventAdapter.notifyDataSetChanged()
                    }
                }
            }
    }


    fun getWeekDates() : ArrayList<String>{
        val now = Calendar.getInstance()
        val format = SimpleDateFormat("dd-MM-yyyy")
        val days = ArrayList<String>(7)
        val delta = -now[GregorianCalendar.DAY_OF_WEEK] + currentWeek //add 2 if your week start on monday
        now.add(Calendar.DAY_OF_MONTH, delta)
        for (i in 0..6) {
            days.add(format.format(now.time))
            now.add(Calendar.DAY_OF_MONTH, 1)
        }

        tv_current_week.text = "${days[0]} - \n${days[6]}"

        return days
    }

}