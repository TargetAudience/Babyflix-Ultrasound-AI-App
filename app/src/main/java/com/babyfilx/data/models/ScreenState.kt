package com.babyfilx.data.models

import com.babyfilx.data.models.response.BlogResponse

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<BlogResponse> = emptyList(),
    val endReached: Boolean = false,
    val page: Int = 1,
    val error: String? = null
)