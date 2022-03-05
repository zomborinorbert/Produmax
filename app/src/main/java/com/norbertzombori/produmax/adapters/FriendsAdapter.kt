package com.norbertzombori.produmax.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.data.Friend
import com.norbertzombori.produmax.data.User

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

        holder.userName.text = user.displayName
        holder.email.text = user.email
    }

    override fun getItemCount() = userList.size


    inner class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val email: TextView = itemView.findViewById(R.id.tv_friends_username)
        val userName: TextView = itemView.findViewById(R.id.tv_friends_email)

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