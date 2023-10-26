package com.babyfilx.data.models.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class BlogResponse(
    @Json(name = "blogBody")
    val blogBody: String = "",
    @Json(name = "blog_image_path")
    val blogImagePath: String,
    @Json(name = "blogTitle")
    val blogTitle: String,
    @Json(name = "blog_url")
    val blogUrl: String?,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "dislike")
    val dislike: String,
    @Json(name = "like")
    var like: Int,
    @Json(name = "nid")
    val nid: String,
    @Json(name = "uid")
    val uid: String,
    @Json(name = "category_name")
    val categoryName: String?=null,
    @Json(name = "user_email")
    val userEmail: String,
    @Json(name = "user_picture")
    val userPicture: String,
    @Json(name = "share_url")
    val shareUrl: String,

    var isLike: Boolean = false
)