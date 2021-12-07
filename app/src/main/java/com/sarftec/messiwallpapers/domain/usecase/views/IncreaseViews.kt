package com.sarftec.messiwallpapers.domain.usecase.views

import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.repository.WallpaperRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class IncreaseViews @Inject constructor(
    private val repository: WallpaperRepository
) : UseCase<IncreaseViews.ViewParam, IncreaseViews.ViewResult>() {

    override suspend fun execute(param: ViewParam?): ViewResult {
        if (param == null) return ViewResult(Resource.error("Error => Increase Views Param NULL!"))
        return ViewResult(repository.increaseViews(param.wallpaper))
    }

    class ViewParam(val wallpaper: MessiWallpaper) : Param
    class ViewResult(val result: Resource<Unit>) : Response
}