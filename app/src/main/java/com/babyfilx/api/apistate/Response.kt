package com.babyfilx.api.apistate

import com.babyfilx.data.models.response.HomeEntriesModel

sealed class Response<T>(val data: T? = null, val message: String = "", val errorData: T? = null) {
    class Success<T>(data: T) : Response<T>(data = data)
    class Error<T>(errorMessage: String,errorData:T?=null) :
        Response<T>(message = errorMessage, errorData = errorData)
    class Loading<T> : Response<T>()
}