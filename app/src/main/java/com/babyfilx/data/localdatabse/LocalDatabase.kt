package com.babyfilx.data.localdatabse

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.babyfilx.data.models.LocalDataModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDatabase @Inject constructor(@ApplicationContext val context: Context) {

    val Context.datastore: DataStore<LocalDataModel> by dataStore(
        fileName = "BabyFlix.json",
        serializer = DatabaseSerializable
    )

    suspend fun setLocalData(data: LocalDataModel) {
        context.datastore.updateData {
            it.copy(
                isLogin = data.isLogin,
                id = data.id,
                image = data.image,
                email = data.email,
                password = data.password,
                lName = data.lName,
                phone = data.phone,
                date = data.date,
                name = data.name,
                token = data.token,
                userType = data.userType
            )
        }
    }

    val data: Flow<LocalDataModel> = context.datastore.data


    suspend fun setToken(token: String) {
        context.datastore.updateData {
            it.copy(tokens = token)
        }
    }

    suspend fun setShareToken(flag: Boolean) {
        context.datastore.updateData {
            it.copy(isShareToken = flag)
        }
    }

    suspend fun setUserType(user: String) {
        context.datastore.updateData {
            it.copy( userType = user)
        }
    }

    suspend fun setLocationIDAndCompany(model: LocalDataModel) {
        context.datastore.updateData {
            it.copy(
                companyId = model.companyId,
                locationId = model.locationId,
                isFirst = model.isFirst,
            )
        }
    }

    suspend fun clear() {
        context.datastore.updateData {
            it.copy(isLogin = false, id = "", isFirst = false)
        }
    }

}