package com.babyfilx.data.localdatabse.room.imageroom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.babyfilx.data.models.response.HomeEntriesModel

@Database(entities = [HomeEntriesModel::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        private const val DATABASE_NAME = "app_image_database"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}