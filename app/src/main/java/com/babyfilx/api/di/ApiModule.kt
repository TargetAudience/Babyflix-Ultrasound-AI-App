package com.babyfilx.api.di

import android.content.Context
import com.babyflix.mobileapp.BuildConfig

import com.babyfilx.utils.exception.NoInternetException
import com.babyfilx.utils.internet.Internet.isInternetAvailable
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.utils.Constant.BASE_URL2
import com.babyfilx.utils.Constant.FireBaseURL
import com.babyflix.mobileapp.R
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class ForUrlDifferentiation2



    @Provides
    @Singleton
    fun provideInterceptors(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return interceptor
    }

    @Provides
    @Singleton
    fun providesHttpLoginInterceptor(@ApplicationContext context: Context): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            if (!isInternetAvailable(context))
                throw NoInternetException(context.getString(R.string.checkInternet))
            val original = chain.request()
            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Content-Type", "application/json")

            chain.proceed(requestBuilder.build())


        }
    }


    @Provides
    @Singleton
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        interceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(interceptor)
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build()
    }


    @Provides
    @Singleton
    fun provideMoshi(): Moshi? {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi?, okHttpClient: OkHttpClient): Retrofit.Builder =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi!!).asLenient())


    @Provides
    @Singleton
    fun provideApis(retrofit: Retrofit.Builder): APIS {
        return retrofit.baseUrl(BuildConfig.BASE_URL).build().create(APIS::class.java)
    }



    @Provides
    @Singleton
    @ForUrlDifferentiation
    fun provideFireBaseApis(retrofit: Retrofit.Builder): APIS {
        return retrofit.baseUrl(FireBaseURL).build().create(APIS::class.java)
    }
}


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForUrlDifferentiation