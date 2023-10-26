package com.babyfilx.base

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.babyflix.mobileapp.BuildConfig
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.LocalDataModel
import com.babyflix.mobileapp.R
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.Forest.plant
import javax.inject.Inject


@HiltAndroidApp
class App : Application() {

    var isAppInForeground = false

    @Inject
    lateinit var localDatabase: LocalDatabase



    companion object {
        var data = LocalDataModel()
        var isFirst by mutableStateOf(false)
    }

    override fun onCreate() {
        super.onCreate()
        gettingLocalDatabase()
        timberInitialization()
        notificationChannel()
        // Register ActivityLifecycleCallbacks to track app state
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                // App is in the foreground
                isAppInForeground = true
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                // App is in the background
                isAppInForeground = false
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    private fun gettingLocalDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            localDatabase.data.collectLatest {
                Timber.e("local database: $it")
                data = it
            }
        }


    }

    private fun timberInitialization() {
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }


    }

    private fun notificationChannel() {
        // Create a Notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.n_channelId),
                getString(R.string.notification_title1),
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                        NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}