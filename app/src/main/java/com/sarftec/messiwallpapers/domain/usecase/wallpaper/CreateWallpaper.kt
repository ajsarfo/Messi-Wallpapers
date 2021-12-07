package com.sarftec.messiwallpapers.domain.usecase.wallpaper

import com.sarftec.messiwallpapers.domain.model.ImageInfo
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.repository.WallpaperRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class CreateWallpaper @Inject constructor(
    private val wallpaperRepository: WallpaperRepository
) : UseCase<CreateWallpaper.CreateParam, CreateWallpaper.CreateResult>() {

    override suspend fun execute(param: CreateParam?): CreateResult {
       val result = param?.let {
           val section = when(it.section) {
               Section.POPULAR -> WallpaperRepository.Section.POPULAR
               Section.BARCA -> WallpaperRepository.Section.BARCA
               Section.PSG -> WallpaperRepository.Section.PSG
               Section.ARGENTINA -> WallpaperRepository.Section.ARGENTINA
           }
           wallpaperRepository.createWallpaper(section, it.imageInfo)
       } ?: Resource.error("Create wallpaper param NULL!")
        return CreateResult(result)
    }

    class CreateParam(
        val section: Section,
        val imageInfo: ImageInfo
    ) : Param

    class CreateResult(val result: Resource<MessiWallpaper>) : Response

    enum class Section {
        POPULAR, BARCA, PSG, ARGENTINA
    }

}