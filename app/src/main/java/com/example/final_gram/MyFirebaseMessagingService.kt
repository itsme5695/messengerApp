package com.example.final_gram

import android.app.NotificationChannel

import android.app.NotificationManager

import android.R

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.content.Intent

import androidx.core.app.NotificationCompat

import com.google.firebase.messaging.RemoteMessage

import com.google.firebase.messaging.FirebaseMessagingService


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)






        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.putExtra("NotificationMessage", "I am from Notification")
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val resultIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            default_notification_channel_id
        )
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentTitle("New message")
            .setContentText(remoteMessage.data["body"])
            .setContentIntent(resultIntent)
            .setAutoCancel(true)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(
            System.currentTimeMillis().toInt(),
            mBuilder.build()
        )
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private const val default_notification_channel_id = "default"
    }
}