package com.sarftec.messiwallpapers.domain.usecase.favorite

import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.repository.FavoriteRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class AlterFavorite @Inject constructor(
    private val repository: FavoriteRepository
) : UseCase<AlterFavorite.FavoriteParam, AlterFavorite.FavoriteResult>() {

    override suspend fun execute(param: FavoriteParam?): FavoriteResult {
        if (param == null) return FavoriteResult(Resource.error("Error => Alter Favorite Param NULL!"))
        return FavoriteResult(
            when(param.option) {
                Option.SAVE -> repository.saveFavorite(param.wallpaper)
                Option.DELETE -> repository.removeFavorite(param.wallpaper)
            }
        )
    }

    class FavoriteParam(val option: Option, val wallpaper: MessiWallpaper) : Param
    class FavoriteResult(val result: Resource<Unit>) : Response

    enum class Option {
        SAVE, DELETE
    }
}