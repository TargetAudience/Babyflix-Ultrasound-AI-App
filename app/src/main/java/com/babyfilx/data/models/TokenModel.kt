package com.babyfilx.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
data class TokenModel(
    val token: String,
    val userId: String
)