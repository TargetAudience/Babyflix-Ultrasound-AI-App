package com.babyfilx.data.models

sealed class FormEvent {
    data class UserName(val name: String) : FormEvent()
    data class Password(val password: String) : FormEvent()
    data class Phone(val phone: String) : FormEvent()
    data class Email(val emil: String) : FormEvent()
    data class CnPassword(val cnPassword: String) : FormEvent()
    data class Accept(val isAccept: Boolean) : FormEvent()
    data class OldPassword(val oldPassword: String) : FormEvent()
    data class FirstName(val firstName: String) : FormEvent()
    data class LastName(val lastName: String) : FormEvent()
    data class PhoneNumber(val phoneNumber: String) : FormEvent()
    data class ExpectedDueDate(val expectedDueDate: String) : FormEvent()
    data class SearchNews(val searchNews: String) : FormEvent()
    object Submit : FormEvent()
    data class ClinicCode(val clinicCode: String) : FormEvent()
}