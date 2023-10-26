package com.babyfilx.utils.ivs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.babyfilx.utils.logs.loge

 class BecomingNoisyReceiver constructor(private val onClick: () -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        loge("this is righs 1")
        if (intent.action.equals("android.media.VOLUME_CHANGED_ACTION")) {
            onClick()
            loge("this is righs")
        }
    }
}