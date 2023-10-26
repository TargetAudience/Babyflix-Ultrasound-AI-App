package com.babyfilx.utils.ivs

import com.babyfilx.api.apiinterface.PlayerAPIS
import retrofit2.Retrofit

fun apiForPlayer( retrofit: Retrofit.Builder,url:String): PlayerAPIS {
    return  Retrofit.Builder().baseUrl(url).build().create(PlayerAPIS::class.java)

}