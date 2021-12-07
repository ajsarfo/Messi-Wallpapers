package com.sarftec.messiwallpapers.domain.usecase.favorite

import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.repository.FavoriteRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class GetFavorites @Inject constructor(
    private val repository: FavoriteRepository
) : UseCase<GetFavorites.FavoriteParam, GetFavorites.FavoriteResult>() {

    override suspend fun execute(param: FavoriteParam?): FavoriteResult {
        if (param == null) return FavoriteResult(Resource.error("Error => Get Favorite param NULL!"))
        return FavoriteResult(repository.getFavorites())
    }

    object FavoriteParam : Param
    class FavoriteResult(val result: Resource<List<MessiWallpaper>>) : Response
}