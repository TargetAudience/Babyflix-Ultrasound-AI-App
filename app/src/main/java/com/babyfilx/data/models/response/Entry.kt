package com.babyfilx.data.models.response


import androidx.annotation.Keep

@Keep
data class Entry(
    val created_at: String,
    val download_url: String,
    val duration: String,
    val mediaTitle: String,
    val mediaType: String,
    val node_id: String,
    val thumb_url: String
)