package com.norbertzombori.produmax.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.data.Friend

class FriendsAdapter(
    private val userList: MutableList<Friend>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsAdapter.FriendsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_friends,
            parent, false
        )

        return FriendsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendsAdapter.FriendsViewHolder, position: Int) {
        val user: Friend = userList[position]

        when {
            user.accepted -> {
                holder.userName.text = user.displayName
            }
            user.sent -> {
                holder.userName.text = "${user.displayName} - not yet accepted"
            }
            else -> {
                holder.userName.text = "${user.displayName} - new friend"
            }
        }
    }

    override fun getItemCount() = userList.size


    inner class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val userName: TextView = itemView.findViewById(R.id.tv_friends_username)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}