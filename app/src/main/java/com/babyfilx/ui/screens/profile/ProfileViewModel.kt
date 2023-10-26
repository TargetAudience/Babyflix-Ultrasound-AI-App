package com.babyfilx.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.data.repositories.AuthenticationRepository
import com.babyfilx.validation.EmailValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App.Companion.data
import com.babyfilx.data.enums.ValidationEnum
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.*
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.validation.EmptyValidation
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val emailValidation: EmailValidation,
    private val emptyValidation: EmptyValidation,
    private val repository: AuthenticationRepository,
    private val localDatabase: LocalDatabase
) : ViewModel() {
    var message by mutableStateOf("")

    var buttonVisible by mutableStateOf(false)
    var state by mutableStateOf(FormState())
    private val apiResponse = Channel<Response<CommanModel>>()
    val response = apiResponse.receiveAsFlow()

    init {
        data.apply {
            state = state.copy(
                email = email,
                firstName = name,
                lastName = lName,
                expectedDueDate = date,
                phoneNumber = phone
            )
        }
    }

    fun formEvent(event: FormEvent) {
        when (event) {
            is FormEvent.FirstName -> {
                state = state.copy(firstName = event.firstName)
            }
            is FormEvent.LastName -> {
                state = state.copy(lastName = event.lastName)
            }
            is FormEvent.PhoneNumber -> {
                state = state.copy(phoneNumber = event.phoneNumber)
            }
            is FormEvent.Email -> {
                state = state.copy(email = event.emil)
            }
            is FormEvent.ExpectedDueDate -> {
                var date = event.expectedDueDate
                /* if (date.length <= 10) {
                     date = if (date.length == 2 || date.length == 5)
                         "$date/"
                     else
                         date
                 }*/
                state = state.copy(expectedDueDate = date)
            }
            is FormEvent.Submit -> {
                submitData()
            }
            else -> {

            }
        }
    }

    private fun submitData() {
        val emailResult = emailValidation.execute(state.email)
        val nameResult = emptyValidation.execute(state.firstName)
        val phoneResult = emptyValidation.execute(state.phoneNumber, ValidationEnum.Phone)
        val lNameResult = emptyValidation.execute(state.lastName, ValidationEnum.Last)
        //val dateResult = emptyValidation.execute(state.expectedDueDate, ValidationEnum.Date)

        val hasError = listOf(
            emailResult,
            nameResult,
            phoneResult,
            lNameResult
        ).any { it.errorMessage != null }
        state = state.copy(
            emailError = emailResult.errorMessage,
            phoneNumberError = phoneResult.errorMessage,
            firstNameError = nameResult.errorMessage,
            lastNameError = lNameResult.errorMessage
        )
        if (hasError)
            return
        viewModelScope.launch {
            apiResponse.send(Response.Loading())
            val map = HashMap<String, Any>()
            map["uid"] = data.id
            map["fname"] = state.firstName
            map["lname"] = state.lastName
            map["password"] = ""
            map["confirm_password"] = ""
            map["email"] = state.email
            map["phone"] = state.phoneNumber

            repository.updateProfileApi(map).catch {
                message = it.message.toString()
                apiResponse.send(Response.Error(errorMessage = it.message.toString()))
            }.collectLatest {
                message = it.message.toString()
                apiResponse.send(Response.Success(it))
                if (it.code == 200)
                    updateData()
            }
        }
    }


    private fun updateData() {
        viewModelScope.launch {
            localDatabase.setLocalData(
                LocalDataModel(
                    isLogin = true,
                    id = data.id,
                    name = state.firstName,
                    token = data.token,
                    password = data.password,
                    email = state.email,
                    lName = state.lastName,
                    phone = state.phoneNumber,
                    date = state.expectedDueDate,
                )
            )
        }
    }

}