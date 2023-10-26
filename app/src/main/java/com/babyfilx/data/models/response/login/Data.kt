package com.babyfilx.data.models.response.login


import androidx.annotation.Keep

@Keep
data class Data(
    val contact: Int?,
    val htmlmail_plaintext: Int?,
    val mimemail_textonly: Int?
)