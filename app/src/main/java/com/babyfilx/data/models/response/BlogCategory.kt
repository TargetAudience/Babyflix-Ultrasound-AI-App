package com.babyfilx.data.models.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class BlogCategory(
    @Json(name = "categories")
    val categories: List<Category> = emptyList()
)