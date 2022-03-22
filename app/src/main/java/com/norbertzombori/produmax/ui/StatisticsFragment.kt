package com.norbertzombori.produmax.ui


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.data.HabitStatistics
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

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.appRepository.getStatistics()
        val arrayAdapter: ArrayAdapter<*>

        var habits : MutableList<String>
        habits = ArrayList()


        arrayAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, habits)
        listview_1.adapter = arrayAdapter



        viewModel.appRepository.eventList.observe(viewLifecycleOwner) { it ->
            it?.forEach{
                habits.add("${it.habitName}: ${getMonthlyNumber(it)}")
                Log.d(ContentValues.TAG, "DJKLASJ DKLASLKDJS l${it.habitName}")
                arrayAdapter.notifyDataSetChanged()
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

        return "This month: $daysInMonth/$monthlyCount  This week 7/$weeklyCount"
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


