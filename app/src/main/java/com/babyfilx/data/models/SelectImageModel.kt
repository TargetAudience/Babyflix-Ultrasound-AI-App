package com.babyfilx.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep


data class SelectImageModel(
    val user_id: String,
    val urls: List<String>,
)