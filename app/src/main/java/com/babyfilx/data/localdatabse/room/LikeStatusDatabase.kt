package com.babyfilx.data.localdatabse.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.babyfilx.data.models.response.LikeStatusRoomModel

@Database(entities = [LikeStatusRoomModel::class], version = 1)
abstract class LikeStatusDatabase : RoomDatabase() {
    abstract fun likeStatusDoa(): LikeStatusDao
}