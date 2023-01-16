package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R


// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(
    messageBody: String,
    downloadStatus: String,
    applicationContext: Context
) {


    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        .putExtra(applicationContext.getString(R.string.file_name_extra), messageBody)
        .putExtra(applicationContext.getString(R.string.status_extra), downloadStatus)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )

        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_launcher_foreground,
            "Check the status",
            contentPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())

}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}