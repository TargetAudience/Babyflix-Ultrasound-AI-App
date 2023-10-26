package com.babyfilx.ui.screens.buyprints

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.data.models.response.HomeModel
import com.babyfilx.data.repositories.HomeRepository
import com.babyfilx.utils.logs.loge
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class BuyPrintsVIewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel(){
    var mainMessage by mutableStateOf("")
    private val apiResponse = Channel<Response<HomeModel>>()
    var images by mutableStateOf(mutableMapOf<String, List<HomeEntriesModel>>())
    var videos by mutableStateOf(mutableMapOf<String, List<HomeEntriesModel>>())
    var all by mutableStateOf(mutableMapOf<String, List<HomeEntriesModel>>())
    init {
        galleyApi()
    }
    private fun galleyApi() {
        viewModelScope.launch {
            loge("final call buy prints 1")
            apiResponse.send(Response.Loading())
            loge("final call buy prints 2")
            repository.galleryApiForHome().catch {
                mainMessage = it.message.toString()
                apiResponse.send(Response.Error(errorMessage = it.message.toString()))
            }.collect { it ->
                all = it.all
                videos = it.videos
                images = it.images
                apiResponse.send(Response.Success(it))
                mainMessage = it.message
                loge("final call buy prints 3")
            }
        }
    }
}