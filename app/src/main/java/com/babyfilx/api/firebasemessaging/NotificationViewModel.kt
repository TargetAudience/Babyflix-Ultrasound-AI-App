package com.babyfilx.api.firebasemessaging

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.data.models.response.HomeModel
import com.babyfilx.data.repositories.HomeRepository
import com.babyfilx.utils.logs.loge
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationViewModel : ViewModel() {
    val showDialogFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun showNotificationDialog() {
        viewModelScope.launch {
            showDialogFlow.emit(true)
        }
    }

    fun dismissDialog() {
        viewModelScope.launch {
            showDialogFlow.emit(false)
        }
    }
}