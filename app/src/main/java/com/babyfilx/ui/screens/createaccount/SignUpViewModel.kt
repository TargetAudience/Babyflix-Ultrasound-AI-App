package com.babyfilx.ui.screens.createaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.data.models.FormEvent
import com.babyfilx.data.models.FormState
import com.babyfilx.validation.EmailValidation
import com.babyfilx.validation.PasswordValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.enums.ValidationEnum
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.LocalDataModel
import com.babyfilx.data.models.response.login.Login
import com.babyfilx.data.repositories.AuthenticationRepository
import com.babyfilx.validation.EmptyValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val emptyValidation: EmptyValidation,
    private val emailValidation: EmailValidation,
    private val passwordValidation: PasswordValidation,
) : ViewModel() {

    var state by mutableStateOf(FormState())

    private val apiResponse = Channel<Response<out Login>>()
    val response = apiResponse.receiveAsFlow()

    // Use another MutableStateFlow to observe navigation event
    private val _navigateToNextScreen = MutableStateFlow(false)
    val navigateToNextScreen: StateFlow<Boolean> = _navigateToNextScreen


    fun formEvent(event: FormEvent) {
        when (event) {
            is FormEvent.FirstName -> {
                state = state.copy(firstName = event.firstName)
            }
            is FormEvent.LastName -> {
                state = state.copy(lastName = event.lastName)
            }
            FormEvent.Submit -> {
                submitData()
            }
            else -> {

            }
        }
    }

    fun formEmailEvent(event: FormEvent) {
        when (event) {
            is FormEvent.Email -> {
                state = state.copy(email = event.emil)
            }
            is FormEvent.PhoneNumber -> {
                state = state.copy(phoneNumber = event.phoneNumber)
            }
            FormEvent.Submit -> {
                submitEmailData()
            }
            else -> {

            }
        }
    }

    fun formMobileEvent(event: FormEvent) {
        when (event) {
            is FormEvent.PhoneNumber -> {
                state = state.copy(phoneNumber = event.phoneNumber)
            }
            FormEvent.Submit -> {
                submitMobileData()
            }
            else -> {

            }
        }
    }

    fun formClinicEvent(event: FormEvent) {
        when (event) {
            is FormEvent.ClinicCode -> {
                state = state.copy(clinicCode = event.clinicCode)
            }
            FormEvent.Submit -> {
                submitClinicData()
            }
            else -> {

            }
        }
    }

    fun formPasswordEvent(event: FormEvent) {
        when (event) {
            is FormEvent.CnPassword -> {
                state = state.copy(password = event.cnPassword)
            }
            FormEvent.Submit -> {
                submitPasswordData()
            }
            else -> {

            }
        }
    }

    private fun submitData() {
        val firstNameResult = emptyValidation.execute(state.firstName)
        val lastNameResult = emptyValidation.execute(state.lastName, ValidationEnum.Last)

        val hasError = listOf(firstNameResult, lastNameResult).any { it.errorMessage != null }
        state = state.copy(
            firstNameError = firstNameResult.errorMessage,
            lastNameError = lastNameResult.errorMessage
        )
        if (hasError)
            return
        else
            _navigateToNextScreen.value = true


    }

    private fun submitEmailData() {
        val emailResult = emailValidation.execute(state.email)
        val mobileResult = emptyValidation.execute(state.phoneNumber, ValidationEnum.Phone)

        val hasError = listOf(emailResult, mobileResult).any { it.errorMessage != null }
        state = state.copy(
            emailError = emailResult.errorMessage,
            phoneNumberError = mobileResult.errorMessage
        )
        if (hasError)
            return
        else
            _navigateToNextScreen.value = true

    }

    private fun submitMobileData() {
        val mobileResult = emptyValidation.execute(state.phoneNumber, ValidationEnum.Phone)

        val hasError = listOf(mobileResult).any { it.errorMessage != null }
        state = state.copy(
            phoneNumberError = mobileResult.errorMessage
        )
        if (hasError)
            return
        else
            _navigateToNextScreen.value = true

    }

    private fun submitClinicData() {
        val clinicCodeResult = emptyValidation.execute(state.clinicCode, ValidationEnum.clinicCode)

        val hasError = listOf(clinicCodeResult).any { it.errorMessage != null }
        state = state.copy(
            clinicCodeError = clinicCodeResult.errorMessage
        )
        if (hasError)
            return
        else
            _navigateToNextScreen.value = true

    }

    private fun submitPasswordData() {
        val passwordResult = passwordValidation.execute(state.password)

        val hasError = listOf(passwordResult).any { it.errorMessage != null }
        state = state.copy(
            passwordError = passwordResult.errorMessage
        )
        if (hasError)
            return
        else
            _navigateToNextScreen.value = true

    }

    // Reset the navigation trigger after navigation
    fun onNavigationComplete() {
        _navigateToNextScreen.value = false
    }
}
