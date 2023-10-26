package com.babyfilx.utils

import android.app.Activity
import android.app.IntentService
import android.app.job.JobService
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.JobIntentService
import androidx.navigation.NavController
import com.babyflix.mobileapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


fun NavController.removeScreen(root: String) {
    popBackStack(graph.startDestinationId, true)
    graph.setStartDestination(root)

}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun Context.shareData(url: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}

fun Long.timeString(): String {
    var totalSeconds: Int = this.toInt() / 1000
    val hours: Int = totalSeconds / 3600
    totalSeconds -= hours * 3600
    val minutes: Int = totalSeconds / 60
    totalSeconds -= minutes * 60
    return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, totalSeconds)
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Context.showDialogs(message: String, onClick: () -> Unit) {
    val builder =
        AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
    builder.setTitle("Delete")
    builder.setMessage(message)

    builder.setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
        onClick()
        dialog.cancel()
    }
    builder.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
        dialog.cancel()
    }
    val alert = builder.create()
    alert.show()
}

fun main() {


    val list = listOf("kallu", "kjkdkjd", "kdfjjf", "jdd", "jdddd")

    val r = list.map { it + "fjjnf" }

    println(get(10, 20, sum))
}

val sum = { x: Int, y: Int -> x + y }
fun get(x: Int, y: Int, d: (Int, Int) -> Int): Int {
    return d(x, y)
}


inline fun set(onClick: () -> Unit) {
    onClick()
}