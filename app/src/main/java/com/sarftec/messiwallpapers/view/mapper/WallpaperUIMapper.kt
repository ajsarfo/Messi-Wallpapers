package com.sarftec.messiwallpapers.view.mapper

import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import javax.inject.Inject

class WallpaperUIMapper @Inject constructor(){

    fun mapToViewUI(wallpaper: MessiWallpaper) : WallpaperUI.Wallpaper {
        return WallpaperUI.Wallpaper(wallpaper)
    }

    fun mapToMessiWallpaper(wallpaperUI: WallpaperUI.Wallpaper) : MessiWallpaper {
        return wallpaperUI.wallpaper
    }
}