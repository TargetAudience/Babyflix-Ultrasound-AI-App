package com.babyfilx.data.models

sealed class EditButtonEvent {
    data class Visible(val remember: Boolean) : EditButtonEvent()
}