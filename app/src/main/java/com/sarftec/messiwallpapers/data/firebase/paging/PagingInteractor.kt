package com.sarftec.messiwallpapers.data.firebase.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sarftec.messiwallpapers.data.DATA_PAGE_SIZE
import com.sarftec.messiwallpapers.data.firebase.repository.wallpaper.FirebaseWallpaperRepository
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PagingInteractor @Inject constructor() {

    fun getWallpaperFlowForId(
        id: Long,
        repository: FirebaseWallpaperRepository
    ): Flow<PagingData<MessiWallpaper>> {
        return Pager(PagingConfig(DATA_PAGE_SIZE.toInt(), enablePlaceholders = false)) {
            FirebaseWallpaperPagingSource(repository, id)
        }.flow
    }
}