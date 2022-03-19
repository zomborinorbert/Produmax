package com.norbertzombori.produmax.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.data.Todo

class TodoAdapter(
    private val todoList: MutableList<Todo>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoAdapter.TodoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_todo,
            parent, false
        )

        return TodoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        val todo: Todo = todoList[position]

        holder.description.text = todo.description
        holder.checkBox.isChecked = todo.done
    }

    override fun getItemCount() = todoList.size


    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {

        val description: TextView = itemView.findViewById(R.id.tv_todo_desc)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_todo_done)

        init {
            checkBox.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
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