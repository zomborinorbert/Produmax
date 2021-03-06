package com.norbertzombori.produmax.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.data.Habit
import com.norbertzombori.produmax.data.Todo

class HabitsAdapter(
    private val habitList: MutableList<Habit>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<HabitsAdapter.HabitsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HabitsAdapter.HabitsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_habit,
            parent, false
        )

        return HabitsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HabitsAdapter.HabitsViewHolder, position: Int) {
        val habit: Habit = habitList[position]

        holder.habitDescription.text = habit.habitDescription
        holder.checkBox.isChecked = habit.done
    }

    override fun getItemCount() = habitList.size


    inner class HabitsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {

        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_done)
        val habitDescription: TextView = itemView.findViewById(R.id.tv_habit_desc)

        init {
            checkBox.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(position)
            }
            return true
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)

        fun onItemLongClick(position: Int)
    }
}