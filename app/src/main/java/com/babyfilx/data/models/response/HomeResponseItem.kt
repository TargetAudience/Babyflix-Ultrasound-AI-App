package com.babyfilx.data.models.response


import androidx.annotation.Keep

@Keep
data class HomeResponseItem(
    val entries: List<Entry>
)