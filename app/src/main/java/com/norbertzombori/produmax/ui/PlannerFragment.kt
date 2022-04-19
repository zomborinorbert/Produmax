package com.norbertzombori.produmax.ui


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.EventAdapter
import com.norbertzombori.produmax.data.Event
import com.norbertzombori.produmax.viewmodels.CreateEventViewModel
import kotlinx.android.synthetic.main.fragment_planner.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class PlannerFragment : Fragment(R.layout.fragment_planner), EventAdapter.OnItemClickListener {
    private val viewModel: CreateEventViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: MutableList<Event>
    private lateinit var dateList: MutableList<String>
    private lateinit var eventAdapter: EventAdapter
    private var currentWeek: Int = -5
    private lateinit var prevMonth: CalendarMonth
    private var setMonth = false

    private var selectedDate: LocalDate? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Planner")
        setMonth = false
        eventChangeListener()
        eventList = ArrayList()
        dateList = ArrayList()
        /*recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)



        eventAdapter = EventAdapter(eventList, this)
        recyclerView.adapter = eventAdapter

        eventChangeListener("${getWeekDates()[0]} 00:00", "${getWeekDates()[6]} 23:59")


         */
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


        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = view.findViewById<TextView>(R.id.calendarDayText)

            init {
                view.setOnClickListener {
                    val reversedDate = "${day.date.toString().split("-")[2]}-${day.date.toString().split("-")[1]}-${day.date.toString().split("-")[0]}"
                    viewModel.selectDay(reversedDate)
                    val action = PlannerFragmentDirections.actionPlannerFragmentToDayViewFragment()
                    findNavController().navigate(action)
                    Log.d(ContentValues.TAG, "DASJMDKLASJDKJASKL ${day.date.toString()} ")
                }
            }
        }

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
                container.day = day
                if(dateList.contains(day.date.toString())){
                    container.textView.setBackgroundResource(R.drawable.calendar_bg)
                }
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.textView.setTextColor(resources.getColor(R.color.black))
                } else {
                    container.textView.setTextColor(resources.getColor(R.color.light_gray))
                }
            }

        }


        calendarView.monthScrollListener = { month ->
            val title = "${month.yearMonth.year} ${monthTitleFormatter.format(month.yearMonth)}"
            Log.d(ContentValues.TAG, "DJDKASJDKLASJKLDASJL $title ")

            tv_current_date.text = title

            if(!setMonth){
                prevMonth = month
                setMonth = true
            }else{
                if(prevMonth.month < month.month){
                    viewModel.setCurrentMonth(1)
                }else{
                    viewModel.setCurrentMonth(-1)
                }
                prevMonth = month
                Log.d(ContentValues.TAG, "ADASJJKLDAKSJDLKASJKLD ${viewModel.currentMonth.value} ")
            }

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                calendarView.notifyDateChanged(it)
            }
        }

        val currentMonth = YearMonth.now().plusMonths(viewModel.currentMonth.value!!.toLong())
        val firstMonth = currentMonth.minusMonths(9)
        val lastMonth = currentMonth.plusMonths(9)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)



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
            .orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED && dc.document.toObject(Event::class.java).accepted) {
                        eventList.add(dc.document.toObject(Event::class.java))
                        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val strDateStart: String = dateFormat.format(Date(dc.document.toObject(Event::class.java).eventDate.seconds * 1000))
                        val date: LocalDate = dc.document.toObject(Event::class.java).eventDate.toDate().toInstant().atZone(
                            ZoneId.systemDefault()).toLocalDate()
                        dateList.add(strDateStart)
                        calendarView.notifyDateChanged(date)
                        Log.d(ContentValues.TAG, "DASJMDKLASJDKJASKL $strDateStart")
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

        return days
    }



}