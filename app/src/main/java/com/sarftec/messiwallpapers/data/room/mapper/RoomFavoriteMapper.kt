package com.sarftec.messiwallpapers.data.room.mapper

import com.sarftec.messiwallpapers.data.room.model.RoomFavorite
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import javax.inject.Inject

class RoomFavoriteMapper @Inject constructor() {

    fun toRoomFavorite(wallpaper: MessiWallpaper) : RoomFavorite {
        return RoomFavorite(wallpaper.id, wallpaper.imageLocation)
    }

    fun toWallpaper(roomFavorite: RoomFavorite) : MessiWallpaper {
        return MessiWallpaper(
            roomFavorite.id,
            imageLocation = roomFavorite.image,
            views = 0,
            section = "",
            isFavorite = true
        )
    }
}