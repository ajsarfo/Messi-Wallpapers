package com.sarftec.messiwallpapers.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarftec.messiwallpapers.data.ROOM_IMAGE_TABLE
import com.sarftec.messiwallpapers.data.room.model.RoomImage

@Dao
interface RoomImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(roomImage: RoomImage)

    @Query("select * from $ROOM_IMAGE_TABLE where id = :id")
    suspend fun getImage(id: String) : RoomImage?

    @Query("delete from $ROOM_IMAGE_TABLE")
    suspend fun clearImages()
}