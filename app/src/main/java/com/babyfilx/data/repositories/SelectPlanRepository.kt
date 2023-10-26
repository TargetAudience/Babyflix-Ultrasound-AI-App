package com.babyfilx.data.repositories

import android.content.Context
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.api.di.ApiModule
import com.babyfilx.api.di.ForUrlDifferentiation
import com.babyfilx.base.App
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.data.models.response.HomeModel
import com.babyfilx.data.models.response.UpgradeUserModel
import com.babyfilx.utils.Constant
import com.babyfilx.utils.Constant.errorMessage
import com.babyfilx.utils.logs.loge
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

class SelectPlanRepository @Inject constructor(

    @Singleton
   private val apis: APIS
) {


    suspend fun upgradeUserToPremium(userId: String, plan: String, planType: String, purchaseToken: String, productId: String
    ): Flow<UpgradeUserModel> = flow {
        val response = apis.upgradeUser(userId, plan, planType, purchaseToken, productId)
        if(response.isSuccessful && response.body() != null){
            emit(UpgradeUserModel(message = response.body()?.message.toString(), code = response.body()?.code.toString(), description = response.body()?.description, expirationDate = response.body()?.expirationDate))
        }else{
            emit(response.body()!!)
        }
    }



}
