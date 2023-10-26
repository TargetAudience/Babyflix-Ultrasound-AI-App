package com.babyfilx.data.models.response

data class HomeModel(
    val all: MutableMap<String, List<HomeEntriesModel>> = mutableMapOf(),
    val videos: MutableMap<String, List<HomeEntriesModel>> = mutableMapOf(),
    val images: MutableMap<String, List<HomeEntriesModel>> = mutableMapOf(),
    val message: String=""
    )
