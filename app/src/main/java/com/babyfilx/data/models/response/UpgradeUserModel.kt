package com.babyfilx.data.models.response

import androidx.annotation.Keep


@Keep
data class UpgradeUserModel(
    val code: String?,
    val description: String?,
    val message: String?,
    val expirationDate: String?
    )
