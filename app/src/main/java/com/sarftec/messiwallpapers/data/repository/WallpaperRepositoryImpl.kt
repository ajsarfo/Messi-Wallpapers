package com.sarftec.messiwallpapers.data.repository

import androidx.paging.PagingData
import com.sarftec.messiwallpapers.data.firebase.mapper.FirebaseWallpaperMapper
import com.sarftec.messiwallpapers.data.firebase.paging.PagingInteractor
import com.sarftec.messiwallpapers.data.firebase.repository.wallpaper.FirebaseBuildWallpaperRepository
import com.sarftec.messiwallpapers.data.firebase.repository.wallpaper.FirebaseWallpaperRepository
import com.sarftec.messiwallpapers.domain.model.ImageInfo
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.repository.WallpaperRepository
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WallpaperRepositoryImpl @Inject constructor(
    private val wallpaperMapper: FirebaseWallpaperMapper,
    private val buildWallpaperRepository: FirebaseBuildWallpaperRepository,
    private val pagingInteractor: PagingInteractor
) : WallpaperRepository {

    override suspend fun getWallpapersForSection(
        section: WallpaperRepository.Section,
        id: Long
    ): Resource<Flow<PagingData<MessiWallpaper>>> {
        return Resource.success(
            pagingInteractor.getWallpaperFlowForId(
                id,
                FirebaseWallpaperRepository(section.id, wallpaperMapper)
            )
        )
    }

    override suspend fun createWallpaper(
        section: WallpaperRepository.Section,
        imageInfo: ImageInfo
    ): Resource<MessiWallpaper> {
       return buildWallpaperRepository.createWallpaper(section , imageInfo)
    }

    override suspend fun deleteWallpaper(wallpaper: MessiWallpaper): Resource<Unit> {
        return buildWallpaperRepository.deleteWallpaper(
            wallpaperMapper.toFirebaseWallpaper(wallpaper)
        )
    }

    override suspend fun increaseViews(wallpaper: MessiWallpaper): Resource<Unit> {
        return buildWallpaperRepository.increaseViews(
            wallpaperMapper.toFirebaseWallpaper(wallpaper)
        )
    }
}