package com.babyfilx.data.repositories

import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.utils.ivs.apiForPlayer
import com.babyfilx.utils.logs.loge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Base64.getEncoder
import javax.inject.Inject

class PlayerRepository @Inject constructor(
    private val retrofit: Retrofit.Builder,
) {


    /**
     * You can delete any user
     */
    suspend fun getPlayingUrl(path: String): Flow<Any> = flow {
        val response = apiForPlayer(retrofit,"$path/").getVideoPlayingUrl()
        if (response.isSuccessful && response.body() != null) {
            loge("getVideoPlayingUrl ${response.body()!!.string()}")
           //emit(saveFile(response.body()!!))


        } else {
            emit(LocationModel(message = response.message()))
        }

    }.flowOn(Dispatchers.IO)



}