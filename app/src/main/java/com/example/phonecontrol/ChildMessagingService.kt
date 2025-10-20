package com.example.phonecontrol

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChildMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data["message"]?.let { message ->
            if(message.isNotEmpty()){
                showNotification(message)
            }
        }
    }

    private fun showNotification(body: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "channelId"
        val channelName = "channelName"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("엄마")
            .setContentText(body ?: "")
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}