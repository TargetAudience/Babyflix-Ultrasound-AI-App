package com.babyfilx.ui.screens.forgot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.data.models.FormEvent
import com.babyfilx.data.models.FormState
import com.babyfilx.validation.EmailValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.data.repositories.AuthenticationRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ForgotViewModel @Inject constructor(
    private val emailValidation: EmailValidation,
    private val repository: AuthenticationRepository
) : ViewModel() {
    var message by mutableStateOf("")

    var state by mutableStateOf(FormState())
    private val apiResponse = Channel<Response<CommanModel>>()
    val response = apiResponse.receiveAsFlow()


    fun formEvent(event: FormEvent) {
        when (event) {
            is FormEvent.Email -> {
                state = state.copy(email = event.emil)
            }
            FormEvent.Submit -> {
                submitData()
            }
            else -> {

            }
        }
    }

    private fun submitData() {
        val emailResult = emailValidation.execute(state.email)

        val hasError = listOf(emailResult).any { it.errorMessage != null }
        if (hasError) {
            state = state.copy(
                emailError = emailResult.errorMessage
            )
            return
        } else {
            state = state.copy(
                emailError = emailResult.errorMessage
            )
        }

        viewModelScope.launch {
            apiResponse.send(Response.Loading())
            repository.forgotPasswordApi(state.email).catch {
                message = it.message.toString()
                apiResponse.send(Response.Error(errorMessage = it.message.toString()))
            }.collectLatest {
                message = it.message.toString()
                apiResponse.send(Response.Success(it))

            }
        }

    }

}