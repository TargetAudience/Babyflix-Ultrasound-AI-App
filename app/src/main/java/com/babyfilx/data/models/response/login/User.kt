package com.babyfilx.data.models.response.login


import androidx.annotation.Keep

@Keep
data class User(
    val access: String,
    val created: String,
    val field_first_name: FieldFirstName,
    val field_last_name: FieldLastName,
    val field_telephone: FieldTelephone,
    val language: String,
    val login: Int,
    val mail: String,
    val name: String,
    val picture: Any?,
    val signature: String,
    val signature_format: Any?,
    val status: String,
    val theme: String,
    val uid: String,
    val uuid: String,
    val roles: Map<String, String>
)