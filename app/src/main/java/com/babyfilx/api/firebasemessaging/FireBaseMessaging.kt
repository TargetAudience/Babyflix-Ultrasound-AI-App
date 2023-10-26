package com.babyfilx.api.firebasemessaging

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.babyfilx.MainActivity
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FireBaseMessaging : FirebaseMessagingService() {

    @Inject
    lateinit var database: LocalDatabase

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            database.setToken(token)
        }
        loge("Token $token")
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

            if (remoteMessage.notification != null) {
                val title = remoteMessage.notification!!.title
                val body = remoteMessage.notification!!.body

                if (title == "AI_Expire" ) {
                    // Show the dialog when a notification is received
                    showNotificationDialog(title, body!!, remoteMessage)
                } else if (title == "AI_Subscribe") {
                    // Show the dialog for AI_Subscribe
                    showNotificationDialog(title, body!!, remoteMessage)
                }else if (title == "Babyflix") {
                    // Show the dialog for Babyflix
                    val intent = Intent("FCM_NOTIFICATION_RECEIVED")
                    intent.putExtra("title", remoteMessage.notification?.title)
                    intent.putExtra("body", remoteMessage.notification?.body)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    sendNotificationImage(remoteMessage)
                }else{
                    sendNotification(remoteMessage = remoteMessage)
                }

            } else if (remoteMessage.data != null) {
                // Send a local broadcast to HomeScreen
                val intent = Intent("FCM_NOTIFICATION_RECEIVED")
                intent.putExtra("title", remoteMessage.data["title"])
                intent.putExtra("body", remoteMessage.data["body"])
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                sendNotificationWithImage(remoteMessage)

            }else{
                loge("Something is wrong")
            }

    }



    private fun showNotificationDialog(title: String, message: String, remoteMessage: RemoteMessage) {
        val intent = Intent("SHOW_NOTIFICATION_DIALOG")
        intent.putExtra("title", title)
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        val requestID = System.currentTimeMillis().toInt()

        val notification = NotificationCompat.Builder(this, getString(R.string.n_channelId))
            .setSmallIcon(R.mipmap.ic_launcher_baby)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(action(requestID))
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(requestID, notification)
    }

    private fun sendNotificationImage(remoteMessage: RemoteMessage) {

        val requestID = System.currentTimeMillis().toInt()

        // Load the image from the URL using Picasso or Glide
        val imageUrl = remoteMessage.notification?.body
        val largeIconBitmap = loadImageFromUrl(imageUrl)

        val notification = NotificationCompat.Builder(this, getString(R.string.n_channelId))
            .setSmallIcon(R.mipmap.ic_launcher_baby)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText("Your image enhanced successfully")
            .setLargeIcon(largeIconBitmap) // Set the image as a large icon
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(largeIconBitmap).bigLargeIcon(null))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(requestID, notification)
    }


    private fun action(requestID: Int): PendingIntent? {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_ONE_SHOT)
        }
    }

    private fun sendNotificationWithImage(remoteMessage: RemoteMessage) {
        // Load the image from the URL using Picasso or Glide
        val imageUrl = remoteMessage.data["body"]
        val largeIconBitmap = loadImageFromUrl(imageUrl)

        val notification = NotificationCompat.Builder(this, getString(R.string.n_channelId))
            .setSmallIcon(R.mipmap.ic_launcher_baby)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText("Your image enhanced successfully")
            .setLargeIcon(largeIconBitmap) // Set the image as a large icon
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(largeIconBitmap).bigLargeIcon(null))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notification)
    }

    // Function to load an image from a URL using Picasso or Glide
    private fun loadImageFromUrl(imageUrl: String?): Bitmap? {
        try {
            // Use Picasso or Glide to load the image from the URL
            val bitmap = Picasso.get().load(imageUrl).get() // Using Picasso
            // val bitmap = Glide.with(this).asBitmap().load(imageUrl).submit().get() // Using Glide
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }



}