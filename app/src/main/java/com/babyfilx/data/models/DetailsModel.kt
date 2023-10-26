package com.babyfilx.data.models

import android.os.Parcelable
import com.babyfilx.data.enums.DrawerContentEnum
import com.babyfilx.data.models.response.HomeEntriesModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailsModel(
    val title: String = "",
    var url: String = "",
    var nodeId: String = "",
    var index: Int = 0,
    var list: MutableList<HomeEntriesModel> = mutableListOf()

) : Parcelable
