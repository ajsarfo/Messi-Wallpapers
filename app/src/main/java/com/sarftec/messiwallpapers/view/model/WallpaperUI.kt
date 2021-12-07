package com.sarftec.messiwallpapers.view.model

import com.sarftec.messiwallpapers.domain.model.MessiWallpaper

sealed class WallpaperUI(val viewType: Int) {
    class Wallpaper(
        val wallpaper: MessiWallpaper
    ) : WallpaperUI(UI_MODEL)

    companion object {
        const val UI_MODEL = 0
        const val UI_AD = 1
    }
}