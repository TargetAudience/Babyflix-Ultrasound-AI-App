package com.babyfilx.data.repositories

import androidx.navigation.NavController
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.base.App
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.LocalDataModel
import com.babyfilx.data.models.Screens
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.data.models.response.UserTypeResponse
import com.babyfilx.data.models.response.login.Login
import com.babyfilx.utils.Constant.errorMessage
import com.babyfilx.utils.logs.loge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(private val apis: APIS) {


    suspend fun loginApi(email: String, password: String): Flow<Login> = flow {
        val response = apis.loginApi(email, password)
        if (response.code() == 200 && response.body() != null)
            emit(response.body()!!)
        else
            emit(Login(message = response.message(), success = false))


    }.flowOn(Dispatchers.IO)


    suspend fun updateProfileApi(map: Map<String, Any>): Flow<CommanModel> = flow {
        val response = apis.updateProfileApi(map)
        if (response.isSuccessful && response.body() != null) {
            emit(errorMessage(response.body().toString()))
        } else {
            emit(CommanModel(message = response.message()))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun forgotPasswordApi(name: String): Flow<CommanModel> = flow {
        val response = apis.forgotPasswordApi(name)
        if (response.isSuccessful && response.body() != null) {
            emit(
                CommanModel(
                    message = "We have sent a reset password request in your mail please check and reset the password.",
                    code = response.code()
                )
            )
        } else {
            emit(CommanModel(message = response.message()))
        }
    }.flowOn(Dispatchers.IO)

}