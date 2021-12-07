package com.sarftec.messiwallpapers.view.listener

import com.sarftec.messiwallpapers.view.model.WallpaperUI
import com.sarftec.messiwallpapers.view.viewmodel.WallpaperViewModel

interface WallpaperFragmentListener {
    fun navigateToDetail(
        section: WallpaperViewModel.Section,
        wallpaper: WallpaperUI.Wallpaper
    )
}