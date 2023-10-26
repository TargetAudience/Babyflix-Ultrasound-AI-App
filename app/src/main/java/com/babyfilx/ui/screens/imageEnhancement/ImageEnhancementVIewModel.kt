package com.babyfilx.ui.screens.imageEnhancement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.data.models.response.HomeModel
import com.babyfilx.data.repositories.HomeRepository
import com.babyfilx.utils.logs.loge
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageEnhancementVIewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel(){

}