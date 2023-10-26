package com.babyfilx.api.di

import android.content.Context
import androidx.room.Room
import com.babyfilx.data.localdatabse.room.LikeStatusDatabase
import com.babyfilx.utils.Constant.LIKE_STATUS_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    //Hilt needs to know how to create an instance of NoteDatabase. For that add another method below provideDao.
    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, LikeStatusDatabase::class.java, LIKE_STATUS_DATABASE
    )
        .build()

    //This annotation marks the method provideDao as a provider of noteDoa.
    @Provides
    @Singleton
    fun provideDao(db: LikeStatusDatabase) = db.likeStatusDoa()


}