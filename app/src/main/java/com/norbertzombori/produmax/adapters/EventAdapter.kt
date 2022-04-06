package com.norbertzombori.produmax.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.data.Event
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private val eventList: MutableList<Event>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventAdapter.EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_events,
            parent, false
        )

        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventAdapter.EventViewHolder, position: Int) {
        val event: Event = eventList[position]


        val dateFormat: DateFormat = SimpleDateFormat("yyyy MMMM dd hh:mm")
        val strDateStart: String = dateFormat.format(Date(event.eventDate.seconds * 1000))
        val strDateEnd: String = dateFormat.format(Date(event.eventDateEnd.seconds * 1000))
        holder.textViewDate.text = "$strDateStart-${strDateEnd.split(' ')[3]}"
        holder.textViewName.text = event.eventName
    }

    override fun getItemCount() = eventList.size


    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val textViewDate: TextView = itemView.findViewById(R.id.tv_item_event_date)
        val textViewName: TextView = itemView.findViewById(R.id.tv_item_event_name)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}