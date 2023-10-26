package com.babyfilx.ui.screens.deeplinking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeepLinkingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,

    ) : ViewModel() {

     val url: String = checkNotNull(savedStateHandle["value"])


}