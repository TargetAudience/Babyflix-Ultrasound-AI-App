package com.babyfilx.ui.screens.imageEnhancement


import androidx.annotation.Keep
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App
import com.babyfilx.base.App.Companion.isFirst
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.response.*
import com.babyfilx.data.repositories.SelectImageRepository
import com.babyfilx.utils.logs.loge
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SelectImagesViewModel @Inject constructor(
    private val repository: SelectImageRepository,
    private val localDatabase: LocalDatabase
):  ViewModel() {
    private val apiResponse = Channel<Response<Any>>()
    val response = apiResponse.receiveAsFlow()

    private val apiResponse2 = Channel<Response<Any>>()
    val response2 = apiResponse2.receiveAsFlow()

    var message by mutableStateOf("")

    // Store the list of images
    private val _images = mutableStateListOf<HomeEntriesModel>()
    val images: List<HomeEntriesModel> get() = _images

    var mainMessage by mutableStateOf("")

    // Track the loading state
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    // Track the loading state
    private val _isRadioButtonActive = mutableStateOf(false)
    val isRadioButtonActive: State<Boolean> = _isRadioButtonActive

    fun setRadioButtonActiveState(isLoading: Boolean) {
        _isRadioButtonActive.value = isLoading
    }

    var isSuccess = true

    // Track the Selected state
    private val _isItemSelected = mutableStateOf(false)
    val isItemSelected: State<Boolean> = _isItemSelected

    fun setItemSelectedState(isLoading: Boolean) {
        _isItemSelected.value = isLoading
    }

    var notificationTitle by mutableStateOf("")
    var notificationMessage by mutableStateOf("")


    init {
        // Call the API when the ViewModel is initialized
//        setUser("premium")
        selectImageApi()
    }

     fun setUser(user : String) {
        viewModelScope.launch {
            localDatabase.setUserType(user)
        }
    }

    private fun selectImageApi() {
        viewModelScope.launch {
            loge("final call 1")
            apiResponse.send(Response.Loading())
            loge("final call 2")
            repository.galleryApiForSelectImage().catch {
                mainMessage = it.message.toString()
                apiResponse.send(Response.Error(errorMessage = it.message.toString()))
            }.collect { it ->
                // Filter the HomeEntriesModel list to exclude items with mediaTitle containing "ai"
                val filteredList = (it as Collection<HomeEntriesModel>).filter { entry ->
                    !entry.mediaTitle.contains("ai", ignoreCase = true)
                }
                apiResponse.send(Response.Success(filteredList))
                isFirst = false
                _images.addAll(filteredList)
            }
        }
    }

    fun enhanceImages(selectedImages: HomeEntriesModel?) {
        setLoadingState(true)
        val imageUrls = selectedImages?.download_url
        val userId = App.data.id

        viewModelScope.launch {
            apiResponse2.send(Response.Loading())

            val gson = Gson()
            val requestBodyJson = gson.toJson(
                EnhanceImagesRequest(listOf(imageUrls!!), userId)
            )

            try {
                val request = Request.Builder()
                    .url("https://3omgxk3pp3.execute-api.us-east-1.amazonaws.com/test/ai_enh_img")
                    .post(requestBodyJson.toRequestBody(MEDIA_TYPE_JSON))
                    .build()

                val client = OkHttpClient()
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    loge("Response for share image: $responseData")

                    // Parse the response body using Gson
                    val parsedResponse = gson.fromJson(responseData, SelectImageResponse::class.java)

                    // Extract the desired information
                    val statusCode = parsedResponse.statusCode
                    val body = parsedResponse.body

                    // Use the extracted values as needed
                    loge("Status Code: $statusCode")
                    loge("Body: $body")

                    if (body != null) {
                        message = body
                    } else {
                        // Handle the case when the body is null
                        message = "Error: Response body is null"
                    }

                    apiResponse2.send(Response.Success(responseData.toString()))
                } else {
                    // Handle unsuccessful response
                    val errorMessage = response.message ?: "Unknown error"
                    apiResponse2.send(Response.Error(errorMessage = errorMessage))
                    loge("Response for share image: $response")
                    message = errorMessage
                    setLoadingState(false)
                }
            } catch (e: IOException) {
                val errorMessage = e.message ?: "Unknown error"
                loge("Response for share image: $e")
                apiResponse2.send(Response.Error(errorMessage = errorMessage))
                message = errorMessage
                setLoadingState(false)
            }
        }
    }

    companion object {
        private val MEDIA_TYPE_JSON = "application/json".toMediaTypeOrNull()
    }
}


@Keep
data class EnhanceImagesRequest(
    val urls: List<String>,
    val user_id: String
)
