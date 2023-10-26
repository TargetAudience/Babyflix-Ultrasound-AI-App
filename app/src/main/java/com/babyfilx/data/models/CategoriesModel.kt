package com.babyfilx.data.models

import android.os.Parcelable
import com.babyfilx.data.enums.DrawerContentEnum
import com.babyfilx.data.models.response.HomeEntriesModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoriesModel(
    val title: String = "",
    var isSelected: Boolean = false

) : Parcelable
