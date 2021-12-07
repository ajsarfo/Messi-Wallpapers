package com.sarftec.messiwallpapers.domain.repository

import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.utils.Resource

interface FavoriteRepository {
    suspend fun getFavorites() : Resource<List<MessiWallpaper>>
    suspend fun saveFavorite(wallpaper: MessiWallpaper) : Resource<Unit>
    suspend fun removeFavorite(wallpaper: MessiWallpaper) : Resource<Unit>
    suspend fun hasFavorite(id: Long) : Resource<Boolean>
}