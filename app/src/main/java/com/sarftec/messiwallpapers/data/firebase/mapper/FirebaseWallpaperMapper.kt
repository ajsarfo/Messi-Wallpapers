package com.sarftec.messiwallpapers.data.firebase.mapper

import com.sarftec.messiwallpapers.data.firebase.model.FirebaseWallpaper
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import javax.inject.Inject

class FirebaseWallpaperMapper @Inject constructor() {

    fun toFirebaseWallpaper(wallpaper: MessiWallpaper) : FirebaseWallpaper {
        return FirebaseWallpaper(
            id = wallpaper.id,
            image = wallpaper.imageLocation,
            views = wallpaper.views,
            section = wallpaper.section
        )
    }

    fun toMessiWallpaper(wallpaper: FirebaseWallpaper) : MessiWallpaper {
        return MessiWallpaper(
            id = wallpaper.id!!,
            imageLocation = wallpaper.image!!,
            views = wallpaper.views ?: 400,
            section = wallpaper.section!!
        )
    }
}