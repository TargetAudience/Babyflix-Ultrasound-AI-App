package com.babyfilx.data.models.response.login


import androidx.annotation.Keep

@Keep
data class Login(
    val sessid: String = "",
    val session_name: String = "",
    val token: String = "",
    val user: User? = null,
    val user_company_id: String = "",
    val message: String="",
    val success: Boolean = true,

    )