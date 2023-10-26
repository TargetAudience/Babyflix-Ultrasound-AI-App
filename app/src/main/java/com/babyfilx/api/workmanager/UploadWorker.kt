package com.babyfilx.api.workmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.babyfilx.data.models.Upload
import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.utils.aws.s3Upload
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.R

class UploadWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        inputData.apply {
            loge("uploadFils Worker")
            //val progress = "Starting Download"
            //setForeground(createForegroundInfo(progress))
            upload(
                getString("file")!!.toUri(),
                LocationModel(
                    company_id = getString("company"),
                    location_id = getString("location")
                )
            )
        }

        return Result.success()
    }

    private fun upload(uri: Uri, model: LocationModel) {
        // Downloads a file and updates bytes read
        // Calls setForeground() periodically when it needs to update
        // the ongoing Notification
        val notification = createForegroundInfo()
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        applicationContext.s3Upload(uri, model) {
            when (it) {
                is Upload.Completed -> {
//                    val pendingIntent: PendingIntent =
//                        Intent(applicationContext, MainActivity::class.java).let { notificationIntent ->
//                            notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                            PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)
//                        }
                    notificationManager.notify(
                        1,
                        notification.setContentTitle("Completed!")
                            .setContentText("File uploaded successfully.").setOngoing(false)
                            .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setProgress(0, 0, false)
                            .build()
                    )
                  //  setForeground(1,notification)
                  //  WorkManager.getInstance(applicationContext).cancelAllWork()
                }
                Upload.Error -> {
                    notificationManager.notify(
                        1,
                        notification.setContentTitle("Failed")
                            .setContentText("Something is wrong please try again.")
                            .setOngoing(false)
                            .setAutoCancel(true)
                            .setProgress(0, 0, false)
                            .build()
                    )
                   // WorkManager.getInstance(applicationContext).can()
                }
                is Upload.Progress -> {
                    notificationManager.notify(
                        1,
                        notification.setContentTitle("Uploading")
                            .setContentText("${it.data}/100").setProgress(100, it.data, false)
                            .build()
                    )

                }
                Upload.Loading -> {
                    notificationManager.notify(
                        1,
                        notification.setContentTitle("Uploading")
                            .setContentText("${0}/100").setProgress(100, 0, false)
                            .build()
                    )
                }
            }
        }
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(): NotificationCompat.Builder {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.cancel)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())
        loge("uploadFils forground")
        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id, title)
        }

        // Add the cancel action to the notification which can
        // be used to cancel the worker
        //.addAction(android.R.drawable.ic_delete, cancel, intent)
        // .build()


        return NotificationCompat.Builder(applicationContext, id)
            // .setContentTitle(title)
            // .setTicker(title)
            //.setContentText(progress)
            .setSmallIcon(R.drawable.logo)
            .setOngoing(true)
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

        notificationManager.createNotificationChannel(channel)
    }


}