package com.sarftec.messiwallpapers.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarftec.messiwallpapers.data.ROOM_FAVORITE_TABLE

@Entity(tableName = ROOM_FAVORITE_TABLE)
class RoomFavorite(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val image: String
)