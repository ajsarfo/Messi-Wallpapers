package com.sarftec.messiwallpapers.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.usecase.favorite.AlterFavorite
import com.sarftec.messiwallpapers.domain.usecase.image.GetImage
import com.sarftec.messiwallpapers.domain.usecase.wallpaper.GetWallpapers
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.mapper.WallpaperUIMapper
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import com.sarftec.messiwallpapers.view.parcel.WallpaperToDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val mapper: WallpaperUIMapper,
    private val getWallpapers: GetWallpapers,
    private val alterFavorite: AlterFavorite,
    getImage: GetImage
) : BaseViewModel<WallpaperUI.Wallpaper>(getImage) {

    private val _wallpaperFlow = MutableLiveData<Resource<Flow<PagingData<WallpaperUI>>>>()
    val wallpaperFlow: LiveData<Resource<Flow<PagingData<WallpaperUI>>>>
        get() = _wallpaperFlow

    private val cacheImageMap = hashMapOf<Int, String>()

    fun loadWallpaperFlow() {
        _wallpaperFlow.value = Resource.loading()
        val parcel = stateHandle.get<WallpaperToDetail>(PARCEL) ?: let {
            _wallpaperFlow.value = Resource.error("Error => parcel not found in state handle!")
            return
        }
        viewModelScope.launch {
            _wallpaperFlow.value = getWallpaperFlow(parcel).let {
                if (it.isSuccess()) mapFlowToViewUI(it.data!!)
                else Resource.error("${it.message}")
            }
        }
    }

    private suspend fun getWallpaperFlow(parcel: WallpaperToDetail): Resource<Flow<PagingData<MessiWallpaper>>> {
        return getWallpapers.execute(getSelection(parcel)).wallpapers
    }

    private fun getSelection(parcel: WallpaperToDetail): GetWallpapers.WallpaperParam {
        val section = when (parcel.section) {
            WallpaperToDetail.POPULAR -> GetWallpapers.Section.POPULAR
            WallpaperToDetail.BARCA -> GetWallpapers.Section.BARCA
            WallpaperToDetail.PSG -> GetWallpapers.Section.PSG
            else -> GetWallpapers.Section.ARGENTINA
        }
        return GetWallpapers.WallpaperParam(section, parcel.wallpaperId)
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

    fun geWallpaperAtPosition(position: Int) : WallpaperUI.Wallpaper? {
        return getAtPosition(position)
    }

    fun setParcel(parcel: WallpaperToDetail) {
        stateHandle.set(PARCEL, parcel)
    }

    private fun mapFlowToViewUI(flow: Flow<PagingData<MessiWallpaper>>): Resource<Flow<PagingData<WallpaperUI>>> {
        return Resource.success(
            flow.map { pagingData ->
                pagingData.map { wallpaper ->
                    mapper.mapToViewUI(wallpaper)
                }
            }
        )
    }

    companion object {
        const val PARCEL = "parcel"
    }
}