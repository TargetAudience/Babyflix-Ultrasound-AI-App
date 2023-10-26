package com.babyfilx.api.apiinterface


import android.provider.MediaStore.Video
import com.babyfilx.data.models.response.HomeResponseItem
import com.babyfilx.data.models.response.HomeResponseX
import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.data.models.response.LoginModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File
import java.io.InputStream
import java.net.URI

interface PlayerAPIS {


    @POST(".")
    suspend fun getVideoPlayingUrl(
    ): Response<ResponseBody>
}
