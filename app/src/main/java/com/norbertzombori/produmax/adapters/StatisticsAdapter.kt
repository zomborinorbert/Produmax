package com.norbertzombori.produmax.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.data.Statistics

class StatisticsAdapter(
    private val statList: MutableList<Statistics>
) :
    RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatisticsAdapter.StatisticsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_statistics,
            parent, false
        )

        return StatisticsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StatisticsAdapter.StatisticsViewHolder, position: Int) {
        val stat: Statistics = statList[position]

        holder.habitDesc.text = stat.habitDescription
        holder.month.text = stat.month
        holder.week.text = stat.week
    }

    override fun getItemCount() = statList.size


    inner class StatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val habitDesc: TextView = itemView.findViewById(R.id.tv_stat_habit_name)
        val month: TextView = itemView.findViewById(R.id.tv_stat_month)
        val week: TextView = itemView.findViewById(R.id.tv_stat_week)
    }
}