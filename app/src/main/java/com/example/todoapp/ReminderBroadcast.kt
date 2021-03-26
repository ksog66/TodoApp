package com.example.todoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcast:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val reminderNotification = NotificationCompat.Builder(
            context!!,"todoChannel").
                setSmallIcon(R.drawable.ic_date_range_black_24dp)
            .setContentTitle(intent?.getStringExtra("list_title"))
            .setContentText(intent?.getStringExtra("todoTask"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(200,reminderNotification.build())

    }
}