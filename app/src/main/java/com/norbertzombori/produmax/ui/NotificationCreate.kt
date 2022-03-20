package com.norbertzombori.produmax.ui


import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.norbertzombori.produmax.R


const val notificationID = 1
const val channelID = "channel1"
var titleExtra = "titleExtra"
var messageExtra = "messageExtra"

class NotificationCreate : BroadcastReceiver(){

    override fun onReceive(p0: Context?, p1: Intent) {
        val notification : Notification? = p0?.let {
            NotificationCompat.Builder(it, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Test notification")
                .setContentText(p1.getStringExtra(messageExtra))
                .build()
        }

        val manager = p0?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }


}