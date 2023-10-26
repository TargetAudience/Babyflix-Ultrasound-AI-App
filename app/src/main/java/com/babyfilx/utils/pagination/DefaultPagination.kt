package com.babyfilx.utils.pagination

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch


class DefaultPagination<Key, Item>(
    private val initialKey: Key,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key) -> Flow<List<Item>>,
    private inline val getNextKey: suspend (List<Item>) -> Key,
    private inline val onError: suspend (Throwable?) -> Unit,
    private inline val onSuccess: suspend (items: List<Item>, nextKey: Key) -> Unit,
) : Pagination<Key, Item> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override suspend fun loadMoreItem() {


        if (isMakingRequest)
            return
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false
        result.catch {
            onError(it)
            onLoadUpdated(false)
        }.collect {
            currentKey = getNextKey(it)
            onSuccess(it, currentKey)
            onLoadUpdated(false)
        }

    }

    override fun reset() {
        currentKey = initialKey
    }

}