package com.babyfilx.data.models.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
@Entity(tableName = "images")
@Parcelize
data class HomeEntriesModel(
    val mediaTitle: String = "",
    val mediaType: String = "",
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val node_id: String = "",
    val thumb_url: String = "",
    val download_url: String = "",
    val duration: String = "",
    val created_at: String = "",
    var isSelect: Boolean=false,
) : Parcelable