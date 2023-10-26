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
import com.babyfilx.data.models.PaymentTokenModel
import com.babyfilx.data.models.TokenModel
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.data.models.response.HomeModel
import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.utils.Constant
import com.babyfilx.utils.Constant.getRealPathFromURI
import com.babyfilx.utils.filepath.FileUtilsModified
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.toast
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

class HomeRepository @Inject constructor(
    private val apis: APIS,
    @ApplicationContext private val context: Context,
    @ForUrlDifferentiation private val tokenAPI: APIS,
    private val database: LocalDatabase
) {

    private var isTokenShare = !App.data.isLogin

    suspend fun galleryApiForHome(): Flow<HomeModel> = flow {
        Timber.e("galleryApiForHome:  ${data.id}")
        val response = apis.galleryApiForHome(data.id)
        if (response.isSuccessful && response.body() != null) {
            emit(convertTheData(response.body()!!.toString()))
        } else {
            emit(HomeModel(message = response.message().toString()))
        }
    }.flowOn(Dispatchers.IO)


    private fun convertTheData(data: String): HomeModel {
        val allMap = mutableMapOf<String, List<HomeEntriesModel>>()
        val imagesMap = mutableMapOf<String, List<HomeEntriesModel>>()
        val videosMap = mutableMapOf<String, List<HomeEntriesModel>>()

        val list = mutableListOf<HomeEntriesModel>()

        val jsonArray = JSONArray(data)
        val result = jsonArray.getJSONObject(0).getJSONArray("entries")
        loge("Final Result ${result}")
        val dates = mutableSetOf<String>()
        for (i in 0 until result.length()) {
            val jsonObject = result.getJSONObject(i)
            val date = jsonObject.optString("created_at")
            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("en"))
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
            loge("Datesss ${model.created_at}")
            loge("Datesss 1 ${dateString}")
            loge("Datesss 3 ${date}")
            loge("Datesss 2 ${model.thumb_url}")
            loge("Datesss 2 ${model.mediaTitle}")
            loge("Datesss 2 ${model.download_url}")
        }
        dates.forEach {
            val all = mutableListOf<HomeEntriesModel>()
            val videos = mutableListOf<HomeEntriesModel>()
            val images = mutableListOf<HomeEntriesModel>()
            list.forEachIndexed { index, model ->
                if (it == model.created_at) {
                    all.add(model)
                    if (model.mediaType == "Video")
                        videos.add(model)
                    else
                        images.add(model)
                }
            }
            allMap[it] = all
            if (videos.isNotEmpty())
                videosMap[it] = videos
            if (images.isNotEmpty())
                imagesMap[it] = images
        }
        return HomeModel(all = allMap, images = imagesMap, videos = videosMap)
    }


    /**
     * You can delete any user
     */
    suspend fun deleteApi(id: String): Flow<CommanModel> = flow {
        val response = apis.deleteContentApi(id)
        if (response.isSuccessful && response.body() != null) {
            emit(Constant.errorMessage(response.body().toString()))
        } else {
            emit(CommanModel(message = response.message()))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * You can delete any user
     */
    suspend fun uploadApi(file: String): Flow<CommanModel> = flow {
        val fileName = context.getRealPathFromURI(file.toUri())!!
        val response =
            apis.uploadContentApi(
                id = data.id,
                file_name = fileName,
                file_chunk = getMultipartImages(file)
            )
        if (response.isSuccessful && response.body() != null) {
            emit(Constant.errorMessage(response.body().toString()))
        } else {
            emit(CommanModel(message = response.message()))
        }

    }.flowOn(Dispatchers.IO)


    /**
     *
     * this function for  images uploading
     *
     */
    private fun getMultipartImages(filePath: String): MultipartBody.Part {
        var image: MultipartBody.Part? = null
        val path =
            FileUtilsModified.getPath(context, Uri.parse(filePath))
        Timber.e("callUploadImageApi: old_driving_license  = $path")
        if (path != null) {
            val file = File(path)
            //  val body = RequestBody.create("image/*".toMediaType(), file)
            val body = file.asRequestBody("*/*".toMediaType())
            image = MultipartBody.Part.createFormData(
                "file_chunk",
                file.name,
                body
            )
        }
        return image!!
    }


    /**
     * You can delete any user
     */
    suspend fun getLocation(): Flow<LocationModel> = flow {
        if (!data.isShareToken) {
            loge("dsakjfjkkj if  ")
            val response = apis.getLocationForUploadContent(data.id)
            if (response.isSuccessful && response.body() != null) {
                val jsonResponse = JSONObject(response.body().toString())
                val model = LocationModel(
                    location_id = jsonResponse.optString("location_id"),
                    company_id = jsonResponse.optString("company_id")
                )
                emit(model)
                //share fcm token only one time for gallery refresh and userType
//                if(!App.data.isShareToken){
                    setToken(model)
                    setPaymentToken(model)
                    database.setShareToken(true)
//                }

            } else {
                emit(LocationModel(message = response.message()))
            }
        } else {
            loge("dsakjfjkkj else")
            emit(
                LocationModel(
                    location_id = data.locationId,
                    company_id = data.companyId,
                )
            )
        }

    }.flowOn(Dispatchers.IO)


    /**
     * this is function for storing token
     */
    private suspend fun setToken(model: LocationModel) {
        if (App.data.tokens.isNotEmpty())
            model.company_id?.let {
                val data = tokenAPI.tokenStore(
                    TokenModel(
                        userId = data.id,
                        token = data.tokens
                    )
                )
                database.setLocationIDAndCompany(
                    LocalDataModel(
                        locationId = model.location_id!!,
                        companyId = model.company_id,
                        isFirst = true
                    )
                )
                loge("messagesdff $data")
            }
    }

    private suspend fun setPaymentToken(model: LocationModel) {
        if (App.data.tokens.isNotEmpty())
            model.company_id?.let {
                val data = apis.paymentTokenStore(PaymentTokenModel(
                    uid = App.data.id,
                    token = App.data.tokens
                ))
                loge("message payment token $data")
            }
    }
}