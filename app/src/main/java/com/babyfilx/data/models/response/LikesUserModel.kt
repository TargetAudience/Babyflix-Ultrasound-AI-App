package com.babyfilx.data.models.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class LikesUserModel(
    @Json(name = "like_status")
    val likeStatus: List<LikeStatu>
)