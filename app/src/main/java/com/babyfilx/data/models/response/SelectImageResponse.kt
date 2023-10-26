package com.babyfilx.data.models.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
data class SelectImageResponse(
    val statusCode: Int,
    val body: String,
)