package com.babyfilx.data.models.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "category_id")
    val categoryId: String,
    @Json(name = "category_name")
    val categoryName: String,
    var isSelected: Boolean = false
)