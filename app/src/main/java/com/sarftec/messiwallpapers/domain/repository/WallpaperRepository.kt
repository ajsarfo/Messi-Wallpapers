package com.sarftec.messiwallpapers.domain.repository

import androidx.paging.PagingData
import com.sarftec.messiwallpapers.domain.model.ImageInfo
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.flow.Flow

interface WallpaperRepository {
    suspend fun getWallpapersForSection(section: Section, id: Long = NO_POSITION) : Resource<Flow<PagingData<MessiWallpaper>>>
    suspend fun createWallpaper(section: Section, imageInfo: ImageInfo) : Resource<MessiWallpaper>
    suspend fun deleteWallpaper(wallpaper: MessiWallpaper) : Resource<Unit>
    suspend fun increaseViews(wallpaper: MessiWallpaper) : Resource<Unit>

    enum class Section(val id: String) {
        POPULAR("popular"), BARCA("barca"), PSG("psg"), ARGENTINA("argentina")
    }

    companion object {
        const val NO_POSITION = -1L
    }
}