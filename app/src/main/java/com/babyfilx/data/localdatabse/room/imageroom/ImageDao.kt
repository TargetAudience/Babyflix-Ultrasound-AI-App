package com.babyfilx.data.localdatabse.room.imageroom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.babyfilx.data.models.response.HomeEntriesModel

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(image: HomeEntriesModel)

    @Query("SELECT * FROM images")
    suspend fun getAllImages(): List<HomeEntriesModel>
}
