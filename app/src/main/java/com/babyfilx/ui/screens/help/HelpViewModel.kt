package com.babyfilx.ui.screens.help

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HelpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,

    ) : ViewModel() {

     val isHelp: Boolean = checkNotNull(savedStateHandle["value"])


}