package com.babyfilx.data.models

import android.net.Uri

sealed class Upload(data: Int = 0, url: String = "") {
    data class Progress(val data: Int) : Upload(data = data)
    data class Completed(val uri: String) : Upload(url = uri)
    object Error : Upload()
    object Loading : Upload()
}