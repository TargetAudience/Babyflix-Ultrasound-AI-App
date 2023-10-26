package com.babyfilx.utils.compressor

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.babyfilx.data.models.Upload
import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.utils.Constant.getRealPathFromURI
import com.babyfilx.utils.logs.loge
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


fun Context.videoCompressor(uri: Uri, onCompress: (Upload) -> Unit) {
    val name = getRealPathFromURI(uri) ?: (1..1000).random().toString()
    VideoCompressor.start(
        context = this, // => This is required
        uris = listOf(uri), // => Source can be provided as content uris
        // THIS STORAGE
        sharedStorageConfiguration = SharedStorageConfiguration(
            saveAt = SaveLocation.movies, // => default is movies
            videoName = name // => required name
        ),

        configureWith = Configuration(
            quality = VideoQuality.MEDIUM,
            isMinBitrateCheckEnabled = true,
            videoBitrateInMbps = 5, /*Int, ignore, or null*/
            disableAudio = false, /*Boolean, or ignore*/
            keepOriginalResolution = false, /*Boolean, or ignore*/
            videoWidth = 360.0, /*Double, ignore, or null*/
            videoHeight = 480.0 /*Double, ignore, or null*/
        ),
        listener = object : CompressionListener {
            override fun onProgress(index: Int, percent: Float) {
                // Update UI with progress value
                loge("compressor onProgress $index $percent")
                val status = (((percent.toDouble() / 100) * 100.0).toInt())
                onCompress(Upload.Progress(status))
            }

            override fun onStart(index: Int) {
                // Compression start
                loge("compressor onStart $index ")
                onCompress(Upload.Loading)
            }

            override fun onSuccess(index: Int, size: Long, path: String?) {
                // On Compression success
                loge("compressor onSuccess $index $size $path")
                path?.let {
                    onCompress(Upload.Completed(uri = path))
                } ?: onCompress(Upload.Error)

            }

            override fun onFailure(index: Int, failureMessage: String) {
                // On Failure
                loge("compressor onFailure $index $failureMessage")

                onCompress(Upload.Error)
            }

            override fun onCancelled(index: Int) {
                // On Cancelled
                loge("compressor onCancelled $index")
                onCompress(Upload.Error)
            }

        }
    )
}


private fun Context.compressVideos(toUri: Uri, locationModel: LocationModel) {
    videoCompressor(uri = toUri) {
        when (it) {
            is Upload.Completed -> {
                //upload(it.uri.toUri(), locationModel)
            }
            Upload.Error -> {
                //upload(toUri, locationModel)
            }
            Upload.Loading -> {
                // progress(data = 0, "Compressing")
            }
            is Upload.Progress -> {
                //progress(data = it.data, "Compressing")
            }
        }
    }
}


fun Context.imageCompress(image: String) {
    val context = this
    CoroutineScope(Dispatchers.IO).launch {
        val compressedImageFile = Compressor.compress(context, File(image))

        loge("compreessImage ${compressedImageFile.path}")
    }

}

