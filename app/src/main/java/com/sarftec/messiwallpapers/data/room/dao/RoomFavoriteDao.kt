package com.sarftec.messiwallpapers.data.room.dao

import androidx.room.*
import com.sarftec.messiwallpapers.data.ROOM_FAVORITE_TABLE
import com.sarftec.messiwallpapers.data.room.model.RoomFavorite

@Dao
interface RoomFavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: RoomFavorite)

    @Query("select * from $ROOM_FAVORITE_TABLE")
    suspend fun getFavorites() : List<RoomFavorite>

    @Query("select * from $ROOM_FAVORITE_TABLE where id = :id")
    suspend fun getFavorite(id: Long) : RoomFavorite?

    @Query("delete from $ROOM_FAVORITE_TABLE where id = :id")
    suspend fun deleteFavorite(id: Long)

}