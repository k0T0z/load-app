package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0
private const val CHANNEL_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, status: Boolean, title: Title) {
    val notifyIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    when (status) {
        true -> notifyIntent.putExtra("status", "Success")
        else -> notifyIntent.putExtra("status", "Failed")
    }
    when (title) {
        Title.GLIDE -> notifyIntent.putExtra("title", applicationContext.getString(R.string.first_radio_button_title))
        Title.LOAD_APP -> notifyIntent.putExtra("title", applicationContext.getString(R.string.second_radio_button_title))
        else -> notifyIntent.putExtra("title", applicationContext.getString(R.string.third_radio_button_title))
    }
    val notifyPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        notifyIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.load_app_notification_channel_id)
    ).setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(notifyPendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
