package com.sarftec.messiwallpapers.data.repository

import com.sarftec.messiwallpapers.data.room.MessiDatabase
import com.sarftec.messiwallpapers.data.room.mapper.RoomFavoriteMapper
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.repository.FavoriteRepository
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val appDatabase: MessiDatabase,
    private val mapper: RoomFavoriteMapper
) : FavoriteRepository {

    override suspend fun getFavorites(): Resource<List<MessiWallpaper>> {
        return Resource.success(
            appDatabase.roomFavoriteDao().getFavorites().map {
                mapper.toWallpaper(it)
            }
        )
    }

    override suspend fun saveFavorite(wallpaper: MessiWallpaper): Resource<Unit> {
        appDatabase.roomFavoriteDao().insertFavorite(
            mapper.toRoomFavorite(wallpaper)
        )
        return Resource.success(Unit)
    }

    override suspend fun removeFavorite(wallpaper: MessiWallpaper): Resource<Unit> {
        appDatabase.roomFavoriteDao().deleteFavorite(wallpaper.id)
        return Resource.success(Unit)
    }

    override suspend fun hasFavorite(id: Long): Resource<Boolean> {
       return Resource.success(
           appDatabase.roomFavoriteDao().getFavorite(id) != null
       )
    }
}