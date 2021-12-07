package com.sarftec.messiwallpapers.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sarftec.messiwallpapers.domain.usecase.favorite.AlterFavorite
import com.sarftec.messiwallpapers.domain.usecase.favorite.GetFavorites
import com.sarftec.messiwallpapers.domain.usecase.favorite.HasFavorite
import com.sarftec.messiwallpapers.domain.usecase.image.GetImage
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.mapper.WallpaperUIMapper
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val mapper: WallpaperUIMapper,
    private val getFavorites: GetFavorites,
    private val alterFavorite: AlterFavorite,
    private val hasFavorite: HasFavorite,
    getImage: GetImage
) : BaseViewModel<WallpaperUI.Wallpaper>(getImage) {

    private val _wallpapers = MutableLiveData<Resource<List<WallpaperUI.Wallpaper>>>()
    val wallpapers: LiveData<Resource<List<WallpaperUI.Wallpaper>>>
        get() = _wallpapers

    fun loadWallpapers() {
        _wallpapers.value = Resource.loading()
        viewModelScope.launch {
            getFavorites.execute(GetFavorites.FavoriteParam).result.let {
                if (it.isSuccess()) {
                    _wallpapers.value = Resource.success(
                        it.data!!.map { wallpaper -> mapper.mapToViewUI(wallpaper) }
                    )
                }
                if (it.isError()) _wallpapers.value = Resource.error("${it.message}")
            }
        }
    }

    fun saveFavorite(wallpaper: WallpaperUI.Wallpaper) {
        viewModelScope.launch {
            alterFavorite.execute(
                AlterFavorite.FavoriteParam(
                    AlterFavorite.Option.SAVE, mapper.mapToMessiWallpaper(wallpaper)
                )
            )
        }
    }

    fun deleteFavorite(wallpaper: WallpaperUI.Wallpaper) {
        viewModelScope.launch {
            alterFavorite.execute(
                AlterFavorite.FavoriteParam(
                    AlterFavorite.Option.DELETE, mapper.mapToMessiWallpaper(wallpaper)
                )
            )
        }
    }

    suspend fun hasFavorite(wallpaper: WallpaperUI.Wallpaper): Boolean {
        return hasFavorite.execute(HasFavorite.FavoriteParam(wallpaper.wallpaper.id)).result.let {
            if(it.isSuccess()) it.data!! else false
        }
    }

    fun geWallpaperAtPosition(position: Int) : WallpaperUI.Wallpaper? {
       return _wallpapers.value?.data?.let {
           it[position]
       }
    }
}