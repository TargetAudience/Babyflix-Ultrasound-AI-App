package com.babyfilx.ui.screens.login

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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.LocalDataModel
import com.babyfilx.data.models.response.login.Login
import com.babyfilx.data.repositories.AuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val emailValidation: EmailValidation,
    private val passwordValidation: PasswordValidation,
    private val api: AuthenticationRepository,
    private val localDatabase: LocalDatabase
) : ViewModel() {

    var state by mutableStateOf(FormState())

    private val apiResponse = Channel<Response<out Login>>()
    val response = apiResponse.receiveAsFlow()


    fun formEvent(event: FormEvent) {
        when (event) {
            is FormEvent.Email -> {
                state = state.copy(email = event.emil)
            }
            is FormEvent.Password -> {
                state = state.copy(password = event.password)
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
        val passwordResult = passwordValidation.execute(state.password)

        val hasError = listOf(emailResult, passwordResult).any { it.errorMessage != null }
        state = state.copy(
            emailError = emailResult.errorMessage,
            passwordError = passwordResult.errorMessage
        )
        if (hasError)
            return

        viewModelScope.launch(Dispatchers.IO) {
            apiResponse.send(Response.Loading())
            api.loginApi(state.email, state.password).catch {
                apiResponse.send(Response.Error(errorMessage = it.message.toString()))
            }.collectLatest { data ->
                if (data.success) {
                    //Check roles and set to userType
                    val rolesMap = data.user?.roles ?: emptyMap()
                    val userType = when {
                        rolesMap.containsKey("6") -> "premium" // "6" represents "Premium Member"
                        rolesMap.containsKey("3") -> "basic"   // "3" represents "Basic Member"
                        else -> "normal"
                    }
                    localDatabase.setLocalData(
                        LocalDataModel(
                            isLogin = true,
                            id = data.user!!.uid,
                            name = data.user.field_first_name.und[0].value,
                            lName = data.user.field_last_name.und[0].value,
                            phone = data.user.field_telephone.und[0].value,
                            image = data.user.picture.toString(),
                            token = data.token,
                            password = state.password,
                            email = state.email,
                            userType = userType
                        )
                    )
                    apiResponse.send(Response.Success(data))
                } else
                    apiResponse.send(Response.Error(errorMessage = data.message))
            }
        }
    }
}