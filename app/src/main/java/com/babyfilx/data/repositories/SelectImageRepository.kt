package com.babyfilx.data.repositories

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.api.di.ForUrlDifferentiation
import com.babyfilx.base.App
import com.babyfilx.base.App.Companion.data
import com.babyfilx.base.App.Companion.isFirst
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.LocalDataModel
import com.babyfilx.data.models.SelectImageModel
import com.babyfilx.data.models.TokenModel
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.data.models.response.HomeModel
import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.utils.Constant
import com.babyfilx.utils.Constant.getRealPathFromURI
import com.babyfilx.utils.filepath.FileUtilsModified
import com.babyfilx.utils.logs.loge
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SelectImageRepository @Inject constructor(
    private val apis: APIS,
    @ApplicationContext private val context: Context,
    @ForUrlDifferentiation private val selectApi: APIS,
    private val database: LocalDatabase
) {


    suspend fun galleryApiForSelectImage(): Flow<Any> = flow {
        val response = apis.galleryApiForHome(data.id)
        if (response.isSuccessful && response.body() != null) {
            emit(convertTheData(response.body()!!.toString()))
        } else {
            emit(HomeModel(message = response.message().toString()))
        }
    }.flowOn(Dispatchers.IO)



    private fun convertTheData(data: String): MutableList<HomeEntriesModel> {

        val list = mutableListOf<HomeEntriesModel>()

        val jsonArray = JSONArray(data)
        val result = jsonArray.getJSONObject(0).getJSONArray("entries")
        loge("Final Result ${result}")
        val dates = mutableSetOf<String>()
        for (i in 0 until result.length()) {
            val jsonObject = result.getJSONObject(i)
            val date = jsonObject.optString("created_at")
            val simpleDateFormat = SimpleDateFormat("HH:mm a | MMMM dd,yyyy", Locale("en"))
            val dateString: String = simpleDateFormat.format(date.toLong() * 1000L)
            val model = HomeEntriesModel(
                node_id = jsonObject.optString("node_id"),
                mediaTitle = jsonObject.optString("mediaTitle"),
                mediaType = jsonObject.optString("mediaType"),
                thumb_url = jsonObject.optString("thumb_url"),
                download_url = jsonObject.optString("download_url"),
                created_at = dateString,
            )
            dates.add(dateString)
            if (model.mediaType == "Image")
                list.add(model)
        }
        return list
    }

    suspend fun galleryApiForRecentScans(): Flow<Any> = flow {
        val response = apis.galleryApiForHome(data.id)
        if (response.isSuccessful && response.body() != null) {
            emit(convertTheDataTo(response.body()!!.toString()))
        } else {
            emit(HomeModel(message = response.message().toString()))
        }
    }.flowOn(Dispatchers.IO)



    private fun convertTheDataTo(data: String): MutableList<HomeEntriesModel> {

        val list = mutableListOf<HomeEntriesModel>()

        val jsonArray = JSONArray(data)
        val result = jsonArray.getJSONObject(0).getJSONArray("entries")
        loge("Final Result ${result}")
        val dates = mutableSetOf<String>()
        for (i in 0 until result.length()) {
            val jsonObject = result.getJSONObject(i)
            val date = jsonObject.optString("created_at")
            val simpleDateFormat = SimpleDateFormat("HH:mm a | MMMM dd,yyyy", Locale("en"))
            val dateString: String = simpleDateFormat.format(date.toLong() * 1000L)
            val model = HomeEntriesModel(
                node_id = jsonObject.optString("node_id"),
                mediaTitle = jsonObject.optString("mediaTitle"),
                mediaType = jsonObject.optString("mediaType"),
                thumb_url = jsonObject.optString("thumb_url"),
                download_url = jsonObject.optString("download_url"),
                created_at = dateString,
            )
            dates.add(dateString)
            list.add(model)
        }
        return list
    }

}