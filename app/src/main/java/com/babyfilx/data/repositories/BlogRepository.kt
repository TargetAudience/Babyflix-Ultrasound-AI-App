package com.babyfilx.data.repositories

import android.content.Context
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.data.models.response.*
import com.babyfilx.utils.internet.Internet.getIpAddress
import com.babyfilx.utils.logs.loge
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject
import kotlin.collections.List

class BlogRepository @Inject constructor(
    private val apis: APIS,
    @ApplicationContext private val context: Context
) {


    suspend fun blogApiForNews(
        page: Int,
        pageSize: Int,
        search: String,
        isCategory: Boolean = false,
        id: String = "0"
    ): Flow<List<BlogResponse>> = flow {

        val response = if (isCategory) apis.getBlogsByTheCategories(id) else apis.blogApiForNews(
            page = page,
            record_per_page = pageSize,
            search = search
        )
        if (response.isSuccessful && response.body() != null) {
            // val data = data(response.body()!!.toString())
            emit(response.body()!!.x1)
            // emit(data)
            loge("Response form news ${response.body().toString()}")
        } else {
            loge("Response form news 1 ${response.body().toString()}")
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    suspend fun categoryApiForNews(): Flow<List<Category>> = flow {
        val response = apis.categoriesApiForNews()
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.categories)
        } else {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getLikesUsers(): Flow<List<LikeStatu>> = flow {
        val response = apis.getLikeBlogsUser()
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.likeStatus)
        } else {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)


    suspend fun addLikeAPi(id: Int): Flow<Any> = flow {
        val response = apis.addLikeApi(nid = id.toLong(), ipAddress = getIpAddress())
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            emit(response.message())
        }
    }.flowOn(Dispatchers.IO)


}