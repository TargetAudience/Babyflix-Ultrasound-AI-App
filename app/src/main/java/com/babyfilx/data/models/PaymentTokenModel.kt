package com.babyfilx.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
data class PaymentTokenModel(
    val token: String,
    val uid: String
)