package com.babyfilx.utils

sealed class HomeUiEvent {
    data class ShowSnackbar(val message: String) : HomeUiEvent()
    // Add more UI events here if needed
}
