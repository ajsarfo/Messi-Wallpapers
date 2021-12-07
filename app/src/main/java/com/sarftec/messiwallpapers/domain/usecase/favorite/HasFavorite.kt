package com.sarftec.messiwallpapers.domain.usecase.favorite

import com.sarftec.messiwallpapers.domain.repository.FavoriteRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class HasFavorite @Inject constructor(
  private val repository: FavoriteRepository
): UseCase<HasFavorite.FavoriteParam, HasFavorite.FavoriteResult>() {

    override suspend fun execute(param: FavoriteParam?): FavoriteResult {
       if(param == null) return FavoriteResult(Resource.error("Error => Has Favorite param NULL!"))
        return FavoriteResult(repository.hasFavorite(param.id))
    }

    class FavoriteParam(val id: Long) : Param
    class FavoriteResult(val result: Resource<Boolean>) : Response
}