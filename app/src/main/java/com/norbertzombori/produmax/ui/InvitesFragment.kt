package com.norbertzombori.produmax.ui

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.norbertzombori.produmax.R
import com.norbertzombori.produmax.adapters.EventAdapter
import com.norbertzombori.produmax.data.Event
import com.norbertzombori.produmax.viewmodels.PlannerViewModel
import kotlinx.android.synthetic.main.fragment_invites.recycler_view
import com.google.firebase.Timestamp

class InvitesFragment : Fragment(R.layout.fragment_invites), EventAdapter.OnItemClickListener {
    private lateinit var viewModel: PlannerViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: MutableList<Event>
    private lateinit var eventAdapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Invitations")
        val user = Firebase.auth.currentUser
        viewModel = PlannerViewModel()
        recyclerView = recycler_view
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)


        eventList = mutableListOf()
        eventAdapter = EventAdapter(eventList, this)
        recyclerView.adapter = eventAdapter

        eventChangeListener(user?.uid!!)
        createLocalNotifications()
    }

    override fun onItemClick(position: Int) {
        showAlertDialogAcceptInvite(position)
    }

    private fun eventChangeListener(userId: String) {
        viewModel.appRepository.db.collection("users").document(userId).collection("events")
            .orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED && !dc.document.toObject(Event::class.java).accepted) {
                        eventList.add(dc.document.toObject(Event::class.java))
                    }
                }

                eventAdapter.notifyDataSetChanged()
            }
    }


    private fun showAlertDialogAcceptInvite(position: Int) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Do you want to accept this invitation?")
            .setPositiveButton("Yes") { _, _ ->
                scheduleNotification(eventList[position].eventDate)
                viewModel.acceptInviteForEvent(eventList[position])
                eventList.removeAt(position)
                eventAdapter.notifyDataSetChanged()
            }.setNegativeButton("No") { _, _ ->

            }.show()
    }

    private fun scheduleNotification(time: Timestamp) {
        val intent = Intent(activity?.applicationContext, NotificationCreate::class.java)
        intent.putExtra(messageExtra, "ACCEPTED INVITATION")

        val pendingIntent = PendingIntent.getBroadcast(
            activity?.applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.seconds * 1000,
            pendingIntent
        )
    }


    private fun createLocalNotifications() {
        val name = "This is the name of is"
        val desc = "This is the description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}