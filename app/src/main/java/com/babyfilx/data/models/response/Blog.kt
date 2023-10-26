package com.babyfilx.data.models.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Blog(
    @Json(name = "blogs")
    val x1: List<BlogResponse> = emptyList()
)