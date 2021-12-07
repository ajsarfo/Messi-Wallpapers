package com.sarftec.messiwallpapers.view.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.sarftec.messiwallpapers.domain.usecase.image.GetImage
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.model.WallpaperUI

abstract class BaseViewModel <T> (
    protected val getImage: GetImage
) : ViewModel() {

    private val cacheImageMap = hashMapOf<Int, T>()

    fun setAtPosition(position: Int, wallpaper: T) {
        cacheImageMap[position] = wallpaper
    }

    fun getAtPosition(position: Int): T? {
        return cacheImageMap[position]
    }

    suspend fun getImage(wallpaperUI: WallpaperUI.Wallpaper): Resource<Uri> {
        return getImage(wallpaperUI.wallpaper.imageLocation)
    }

    suspend fun getImage(imageLocation: String): Resource<Uri> {
        return getImage.execute(
            GetImage.GetImageParam(imageLocation)
        ).image
    }
}