package com.hiskytech.selfmademarket.Model

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Ui.MainActivity


const val CHANNEL_ID_2 = "NOTIFICATION_CHANNEL"
const val CHANNEL_NAME_2 = "MESSAGE_NOTIFICATION"

class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.data.isNotEmpty()) {
        }
        if (message.notification != null) {
            generateNotification(
                message.notification!!.title.toString(),
                message.notification!!.body.toString()
            )
        }


    }

    fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID_2)
            .setSmallIcon(R.drawable.logo_for_login_screen)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //check oreo or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID_2, CHANNEL_NAME_2, NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(notificationChannel)
        }

        //Check Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(0, builder.build())
            }
        } else {
            notificationManager.notify(0, builder.build())
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, message: String): RemoteViews {

        val remoteView = RemoteViews(packageName, R.layout.notification_layout)

        remoteView.setTextViewText(R.id.notification_title, title)
        remoteView.setTextViewText(R.id.notification_message, message)
        remoteView.setImageViewResource(R.id.notification_logo, R.drawable.ic_launcher_background)
        return remoteView
    }
}