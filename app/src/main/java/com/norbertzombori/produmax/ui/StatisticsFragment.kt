package com.norbertzombori.produmax.ui


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.EventAdapter
import com.norbertzombori.produmax.adapters.StatisticsAdapter
import com.norbertzombori.produmax.data.*
import com.norbertzombori.produmax.viewmodels.StatisticsViewModel
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalField
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var statList: MutableList<Statistics>
    private lateinit var statisticsAdapter: StatisticsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Statistics")

        viewModel.appRepository.getStatistics()
        val arrayAdapter: ArrayAdapter<*>

        var habits : MutableList<String>
        habits = ArrayList()
        statList = ArrayList()


        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)



        statisticsAdapter = StatisticsAdapter(statList)
        recyclerView.adapter = statisticsAdapter



        viewModel.appRepository.eventList.observe(viewLifecycleOwner) { it ->
            it?.forEach{
                val current = "${it.habitName}: ${getMonthlyNumber(it)}"
                if(!habits.contains(current)){
                    habits.add(current)
                    statList.add(Statistics(it.habitName, getMonthlyNumber(it).split("*")[0], getMonthlyNumber(it).split("*")[1]))
                }
                Log.d(ContentValues.TAG, "DJKLASJ DKLASLKDJS l${it.habitName}")
                statisticsAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun getMonthlyNumber(habit: HabitStatistics) : String{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        val yearMonthObject: YearMonth = YearMonth.of(formatted.split("-")[0].toInt(), formatted.split("-")[1].toInt())
        val daysInMonth: Int = yearMonthObject.lengthOfMonth()
        var monthlyCount = 0
        var weeklyCount = 0

        var currentWeekDates = getWeekDates()

        for(date in habit.dateList){
            if(date.split("-")[1].toInt() == formatted.split("-")[1].toInt()){
                monthlyCount++
            }
            if(date in currentWeekDates){
                weeklyCount++
            }
        }

        return "This month: $daysInMonth/$monthlyCount*This week 7/$weeklyCount"
    }

    fun getWeekDates() : ArrayList<String>{
        val now = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd")
        val days = ArrayList<String>(7)
        val delta = -now[GregorianCalendar.DAY_OF_WEEK] + 2 //add 2 if your week start on monday
        now.add(Calendar.DAY_OF_MONTH, delta)
        for (i in 0..6) {
            days.add(format.format(now.time))
            now.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

}


