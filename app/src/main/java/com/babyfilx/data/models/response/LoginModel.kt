package com.babyfilx.data.models.response


import androidx.annotation.Keep

@Keep
data class LoginModel(
    val sessid: String?,
    val session_name: String?,
    val token: String?,
    val user: User
)


@Keep
data class User(
    val access: String,
    val created: String,
    val `data`: Data?,
    val fullname: String?,
    val language: String?,
    val login: Int?,
    val mail: String?,
    val name: String,
    val picture: Any?,
    val signature: String?,
    val signature_format: String?,
    val status: String?,
    val theme: String?,
    val timezone: String?,
    val uid: String,
    val uuid: String?,
    val roles: Map<String, String>
)

@Keep
data class Data(
    val contact: String?,
    val description: String?,
    val doctor_id: String?,
    val htmlmail_plaintext: Any?,
    val mimemail_textonly: Any?,
    val overlay: Int?,
    val practice_name: String?,
    val price: String?,
    val terms: String?
)


