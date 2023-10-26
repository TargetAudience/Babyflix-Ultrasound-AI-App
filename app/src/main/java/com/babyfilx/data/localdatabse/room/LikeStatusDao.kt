package com.babyfilx.data.localdatabse.room

import androidx.room.*
import com.babyfilx.data.models.response.LikeStatusRoomModel

@Dao
interface LikeStatusDao {

    @Query("SELECT * FROM like_status")
   suspend fun getAllLikeStatus(): List<LikeStatusRoomModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStatus(model: LikeStatusRoomModel)

    @Delete
    fun delete(like: LikeStatusRoomModel)
}