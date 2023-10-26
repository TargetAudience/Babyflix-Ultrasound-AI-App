package com.babyfilx.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
data class UserStatusModel(
    val uid: String
)