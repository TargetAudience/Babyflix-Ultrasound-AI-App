package com.babyfilx.api.workmanager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Message
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.babyfilx.MainViewModel
import com.babyfilx.data.models.Upload
import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.utils.aws.s3Upload
import com.babyfilx.utils.compressor.videoCompressor
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UploadService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    var notificationManager1: NotificationManager? = null

    private val mainActivityScope = CoroutineScope(Dispatchers.Main)

    val intentComplete = Intent("UPLOAD_COMPLETED")

    val intentError = Intent("UPLOAD_ERROR")



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        loge("jsndjkfjsdjf")
        intent?.let {
            it.apply {
                if (ACTION_STOP_SERVICE == it.action) {
                    loge("called to cancel service")
                    notificationManager?.cancel(1)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }else {
                    loge("File Uri "+getStringExtra("file")!!.toUri())
                    loge("File Uri "+getStringExtra("company")!!.toUri())
                    loge("File Uri "+getStringExtra("location")!!.toUri())
                    upload(
                        getStringExtra("file")!!.toUri(),
                        LocationModel(
                            company_id = getStringExtra("company"),
                            location_id = getStringExtra("location")
                        )
                    )
                }
            }
        }


        return START_NOT_STICKY
    }

    private var notification: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManagerCompat? = null
    var ACTION_STOP_SERVICE = "STOP"
    override fun onCreate() {
        super.onCreate()
        notificationManager1 =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
        notification = createForegroundInfo()
        notificationManager = NotificationManagerCompat.from(applicationContext)

        startForeground(
            1,
            notification!!.setContentTitle("Uploading")
                .setContentText("${0}/100").setProgress(100, 0, false)
                .build()
        )
    }

    private fun upload(uri: Uri, model: LocationModel) {
        // Downloads a file and updates bytes read
        // Calls setForeground() periodically when it needs to update
        // the ongoing Notification


        applicationContext.s3Upload(uri, model) {
            when (it) {
                is Upload.Completed -> {
                    // Inside the UploadService, after the upload is completed:
                    success()
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intentComplete)
                }
                Upload.Error -> {
                    error()
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intentError)
                }
                is Upload.Progress -> {
                    notificationManager!!.notify(
                        1,
                        notification!!.setContentTitle("Uploading")
                            .setContentText("${it.data}/100").setProgress(100, it.data, false)
                            .build()
                    )
                }
                Upload.Loading -> {}

            }
        }
    }

    private fun error(message: String = "Something is wrong please try again.") {
        notificationManager!!.notify(
            2,
            notification!!.setContentTitle("Failed")
                .setContentText(message)
                .setOngoing(false)
                .setAutoCancel(true)
                .setProgress(0, 0, false)
                .build()
        )
        // WorkManager.getInstance(applicationContext).can()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun success(message: String = "File uploaded successfully.") {
//        notificationManager!!.notify(
//            2,
//            notification!!.setContentTitle("Completed!")
//                .setContentText(message).setOngoing(false)
//                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setProgress(0, 0, false)
//                .build()
//        )
        //  WorkManager.getInstance(applicationContext).cancelAllWork()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }



    // Creates an instance of ForegroundInfo which can be used to update the
// ongoing notification.
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createForegroundInfo(): NotificationCompat.Builder {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.cancel)
        // This PendingIntent can be used to cancel the worker
        /* val intent = WorkManager.getInstance(applicationContext)
             .createCancelPendingIntent(getId())*/
        loge("uploadFils forground")
        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id, title)
        }

        // Add the cancel action to the notification which can
        // be used to cancel the worker
        //.addAction(android.R.drawable.ic_delete, cancel, intent)
        // .build()
        /*val pendingIntent: PendingIntent =
            Intent(applicationContext, MainActivity::class.java).let { notificationIntent ->
                notificationIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)
            }*/
        val intent = Intent(this, UploadService::class.java).apply {
            action = ACTION_STOP_SERVICE
            putExtra(EXTRA_NOTIFICATION_ID, 1)
        }
        val pIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        return NotificationCompat.Builder(applicationContext, id)
            // .setContentTitle(title)
            // .setTicker(title)
            //.setContentText(progress)
            .setSmallIcon(R.mipmap.ic_launcher_baby)
            .setOngoing(true)
            .setColor(applicationContext.getColor(R.color.white))
            // .addAction(R.drawable.screen_cast, "Cancel", pIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        //return ForegroundInfo(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(id: String, title: String) {
        // Create a Notification channel
        val channel = NotificationChannel(
            id,
            title,
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager1!!.createNotificationChannel(channel)
    }
}