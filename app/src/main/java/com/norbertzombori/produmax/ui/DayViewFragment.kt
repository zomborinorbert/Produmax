package com.norbertzombori.produmax.ui


import android.os.Bundle
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
import com.norbertzombori.produmax.viewmodels.PlannerViewModel
import kotlinx.android.synthetic.main.fragment_day_view.recycler_view
import java.text.SimpleDateFormat
import java.util.*

class DayViewFragment : Fragment(R.layout.fragment_day_view), EventAdapter.OnItemClickListener {
    private val viewModel: PlannerViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: MutableList<Event>
    private lateinit var eventAdapter: EventAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Planner")
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)


        eventList = ArrayList()
        eventAdapter = EventAdapter(eventList, this)
        recyclerView.adapter = eventAdapter

        eventChangeListener(
            "${viewModel.selectedDay.value} 00:00",
            "${viewModel.selectedDay.value} 23:59"
        )
    }

    override fun onItemClick(position: Int) {
        viewModel.select(eventList[position])
        val action = DayViewFragmentDirections.actionDayViewFragmentToEventDetailsFragment()
        findNavController().navigate(action)
    }

    fun eventChangeListener(sdate: String = "1-4-2022 9:5", edate: String = "28-4-2022 11:5") {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val date1 = dateFormat.parse(sdate)
        val date2 = dateFormat.parse(edate)
        val startDate = Timestamp(date1)
        val endDate = Timestamp(date2)

        viewModel.plannerRepository.db.collection("users")
            .document(viewModel.plannerRepository.firebaseAuth.currentUser?.uid!!)
            .collection("events")
            .whereGreaterThan("eventDate", startDate)
            .whereLessThan("eventDate", endDate)
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

}