package com.babyfilx.utils.extentions

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.ui.screens.player.PlayerViewModel
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase


fun Context.toast(message: Any, length:Int=Toast.LENGTH_SHORT) {
    Toast.makeText(this, message.toString(), length).show()
}

/**
 * this is for download
 */
fun Context.download(url: HomeEntriesModel) {
    try {
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url.download_url)

        val request = DownloadManager.Request(downloadUri).apply {
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                url.mediaTitle + title(url.mediaType)
            )
        }
        downloadManager.enqueue(request)
    } catch (e: Exception) {
        val cr = Firebase.crashlytics
        cr.setCustomKey("Download", e.message.toString())
        cr.recordException(e)
    }

}

fun title(type: String): String {
    return if (type == "Video") ".mp4" else ".png"
}