package com.babyfilx.ui.screens.splash

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.data.models.FormEvent
import com.babyfilx.data.models.FormState
import com.babyfilx.data.repositories.AuthenticationRepository
import com.babyfilx.validation.EmailValidation
import com.babyfilx.validation.PasswordValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.LocalDataModel
import com.babyfilx.data.models.response.LoginModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
) : ViewModel() {

    private var _state = MutableSharedFlow<Boolean>()
    val state = _state.asSharedFlow()


    init {
        viewModelScope.launch {
            Timber.e("sd dddd")
            delay(3000)
            _state.emit(true)
            Timber.e("sd dddd 1")

        }

    }

}