package com.babyfilx.utils.internet

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.BuildConfig
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


object Internet {

    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
        return result
    }


    fun getIpAddress(): String {
        var ip = ""
        try {
            val enumNetworkInterfaces: Enumeration<NetworkInterface> = NetworkInterface
                .getNetworkInterfaces()
            while (enumNetworkInterfaces.hasMoreElements()) {
                val networkInterface: NetworkInterface = enumNetworkInterfaces
                    .nextElement()
                val enumInetAddress: Enumeration<InetAddress> = networkInterface
                    .inetAddresses
                while (enumInetAddress.hasMoreElements()) {
                    val inetAddress: InetAddress = enumInetAddress.nextElement()
                    if (inetAddress.isSiteLocalAddress) {
                        ip += inetAddress.hostAddress
                    }
                }
            }
        } catch (e: SocketException) {
            val randomHost = (10..99).random()
            ip = "168:192:1:$randomHost"
            e.printStackTrace()
            Firebase.crashlytics.recordException(e)

        }
        loge(
            "Ipaddess $ip"
        )
        return ip
    }
}