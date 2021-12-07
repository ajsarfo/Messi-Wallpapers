package com.sarftec.messiwallpapers.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sarftec.messiwallpapers.data.room.converter.UriConverter
import com.sarftec.messiwallpapers.data.room.dao.RoomFavoriteDao
import com.sarftec.messiwallpapers.data.room.dao.RoomImageDao
import com.sarftec.messiwallpapers.data.room.model.RoomFavorite
import com.sarftec.messiwallpapers.data.room.model.RoomImage

@Database(
    entities = [RoomImage::class, RoomFavorite::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(UriConverter::class)
abstract class MessiDatabase : RoomDatabase() {
    abstract fun roomImageDao() : RoomImageDao
    abstract fun roomFavoriteDao() : RoomFavoriteDao

    companion object {
        fun getInstance(context: Context): MessiDatabase {
            return Room.databaseBuilder(
                context,
                MessiDatabase::class.java,
                "messi_database.db"
            ).build()
        }
    }
}