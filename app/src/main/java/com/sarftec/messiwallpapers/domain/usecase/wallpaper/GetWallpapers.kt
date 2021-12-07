package com.sarftec.messiwallpapers.domain.usecase.wallpaper

import androidx.paging.PagingData
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.repository.WallpaperRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWallpapers @Inject constructor(
    private val repository: WallpaperRepository
) : UseCase<GetWallpapers.WallpaperParam, GetWallpapers.WallpaperResult>() {

    override suspend fun execute(param: WallpaperParam?): WallpaperResult {
        if (param == null) return WallpaperResult(Resource.error("Get wallpaper param NULL!"))
        val section = when (param.section) {
            Section.POPULAR -> WallpaperRepository.Section.POPULAR
            Section.BARCA -> WallpaperRepository.Section.BARCA
            Section.PSG -> WallpaperRepository.Section.PSG
            Section.ARGENTINA -> WallpaperRepository.Section.ARGENTINA
        }
        return WallpaperResult(
            repository.getWallpapersForSection(section, param.id)
        )
    }

    class WallpaperParam(val section: Section, val id: Long = -1) : Param
    class WallpaperResult(
        val wallpapers: Resource<Flow<PagingData<MessiWallpaper>>>
    ) : Response

    enum class Section {
        POPULAR, BARCA, PSG, ARGENTINA
    }
}