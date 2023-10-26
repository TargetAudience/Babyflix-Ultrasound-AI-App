package com.babyfilx.data.models.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity("like_status")
data class LikeStatusRoomModel(
    @PrimaryKey val likeEntityId: String,
    val count: String,
)