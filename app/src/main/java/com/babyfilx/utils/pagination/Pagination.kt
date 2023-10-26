package com.babyfilx.utils.pagination

interface Pagination<Key, Item>{
    suspend fun loadMoreItem()
    fun reset()
}