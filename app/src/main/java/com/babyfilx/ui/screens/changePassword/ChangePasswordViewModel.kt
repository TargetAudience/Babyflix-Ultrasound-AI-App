package com.babyfilx.ui.screens.changePassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.data.models.FormEvent
import com.babyfilx.data.models.FormState
import com.babyfilx.data.repositories.AuthenticationRepository
import com.babyfilx.validation.PasswordValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.validation.RepeatPasswordValidation
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val passwordValidation: PasswordValidation,
    private val repeatPasswordValidation: RepeatPasswordValidation,
    private val api: APIS,
    private val repository: AuthenticationRepository,
) : ViewModel() {
    var message by mutableStateOf("")
    var state by mutableStateOf(FormState())
    private val apiResponse = Channel<Response<CommanModel>>()
    val response = apiResponse.receiveAsFlow()

    fun formEvent(event: FormEvent) {
        when (event) {
            is FormEvent.Password -> {
                state = state.copy(password = event.password)
            }
            is FormEvent.CnPassword -> {
                state = state.copy(cnPassword = event.cnPassword)
            }
            is FormEvent.OldPassword -> {
                state = state.copy(oldPassword = event.oldPassword)
            }
            FormEvent.Submit -> {
                submitData()
            }
            else -> {

            }
        }
    }

    private fun submitData() {
        val password = passwordValidation.execute(state.password)
        val confirmpassword = repeatPasswordValidation.execute(state.password, state.cnPassword)
        val oldpassword = passwordValidation.execute(state.oldPassword)

        val hasError =
            listOf(password, confirmpassword, oldpassword).any { it.errorMessage != null }
        state = state.copy(
            passwordError = password.errorMessage,
            cnPasswordError = confirmpassword.errorMessage,
            oldPasswordError = oldpassword.errorMessage
        )
        if (hasError)
            return



        viewModelScope.launch {
            viewModelScope.launch {
                apiResponse.send(Response.Loading())
                val map = HashMap<String, Any>()
                map["uid"] = App.data.id
                map["fname"] = ""
                map["lname"] = ""
                map["password"] = state.oldPassword
                map["confirm_password"] = state.cnPassword
                map["email"] = ""
                map["phone"] = ""

                repository.updateProfileApi(map).catch {
                    message = it.message.toString()
                    apiResponse.send(Response.Error(errorMessage = it.message.toString()))
                }.collectLatest {
                    message = it.message.toString()
                    apiResponse.send(
                        Response.Success(it))
                }
            }
        }
    }
}