package com.babyfilx.data.models

@kotlinx.serialization.Serializable
data class LocalDataModel(
    val isLogin: Boolean = false,
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val token: String = "",
    val password: String = "",
    val lName: String = "",
    val date: String = "",
    val email: String = "",
    val phone: String = "",
    val tokens: String = "",
    val locationId: String = "",
    val companyId: String = "",
    val isFirst: Boolean = false,
    val userType: String = "basic",
    val isShareToken: Boolean = false,
)
