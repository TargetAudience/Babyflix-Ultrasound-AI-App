package com.babyfilx.data.models.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class LikeStatu(
    @Json(name = "like_entity_id")
    val likeEntityId: String,
    @Json(name = "like_user_id")
    val likeUserId: String
)