package com.babyfilx.data.models

data class FormState(
    var userName: String = "",
    var phone: String = "",
    val password: String = "",
    val cnPassword: String = "",
    val oldPassword: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val expectedDueDate: String = "",
    val searchNews: String = "",
    val isAccept: Boolean = false,
    val userNameError: String? = null,
    val isAcceptError: String? = null,
    val passwordError: String? = null,
    val emailError: String? = null,
    val cnPasswordError: String? = null,
    val oldPasswordError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNumberError: String? = null,
    val expectedDueDateError: String? = null,
    val searchNewsError: String? = null,
    val clinicCode: String = "",
    val clinicCodeError: String? = null,
)