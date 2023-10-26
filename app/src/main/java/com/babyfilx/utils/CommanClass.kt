package com.babyfilx.utils

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.base.App
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.LocalDataModel
import com.babyfilx.data.models.Screens
import com.babyfilx.data.models.response.UserTypeResponse
import com.babyfilx.utils.logs.loge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommanClass @Inject constructor(private val apis: APIS)  {


}