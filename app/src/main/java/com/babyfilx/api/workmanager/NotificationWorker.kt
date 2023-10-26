package com.babyfilx.api.workmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.babyfilx.data.models.Upload
import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.utils.aws.s3Upload
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.R

class NotificationWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    val NOTIFICATION_ACTION = "com.babyfilx.NOTIFICATION_ACTION"

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        val notificationType = inputData.getString("notificationType")

        val intent = Intent(NOTIFICATION_ACTION)
        intent.putExtra("notificationType", notificationType)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        return Result.success()
    }




}