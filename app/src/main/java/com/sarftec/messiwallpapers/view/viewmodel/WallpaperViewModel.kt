package com.sarftec.messiwallpapers.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.usecase.image.GetImage
import com.sarftec.messiwallpapers.domain.usecase.views.IncreaseViews
import com.sarftec.messiwallpapers.domain.usecase.wallpaper.GetWallpapers
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.manager.NetworkManager
import com.sarftec.messiwallpapers.view.mapper.WallpaperUIMapper
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import com.sarftec.messiwallpapers.view.utils.NETWORK_ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WallpaperViewModel @Inject constructor(
    private val wallpaperUIMapper: WallpaperUIMapper,
    private val getWallpapers: GetWallpapers,
    private val increaseViews: IncreaseViews,
    private val networkManager: NetworkManager,
    getImage: GetImage,
) : BaseViewModel<WallpaperUI.Wallpaper>(getImage) {

    private val _wallpaperFlow = MutableLiveData<Resource<Flow<PagingData<WallpaperUI>>>>()
    val wallpaperFlow: LiveData<Resource<Flow<PagingData<WallpaperUI>>>>
        get() = _wallpaperFlow

    fun loadWallpapers(section: Section) {
        _wallpaperFlow.value = Resource.loading()
        val selectionType = when (section) {
            Section.POPULAR -> GetWallpapers.Section.POPULAR
            Section.BARCA -> GetWallpapers.Section.BARCA
            Section.PSG -> GetWallpapers.Section.PSG
            Section.ARGENTINA -> GetWallpapers.Section.ARGENTINA
        }
        viewModelScope.launch {
            getWallpapers.execute(GetWallpapers.WallpaperParam(selectionType)).let {
                _wallpaperFlow.value =
                    if (it.wallpapers.isSuccess()) it.wallpapers.data?.let { flow ->
                        mapFlowToViewUI(flow)
                    }
                    else Resource.error(it.wallpapers.message)
            }
        }
    }

    fun hasNetwork() : Boolean {
        return networkManager.isNetworkAvailable()
    }

    fun wallpaperClicked(wallpaper: WallpaperUI.Wallpaper) {
        viewModelScope.launch {
            increaseViews.execute(
                IncreaseViews.ViewParam(wallpaperUIMapper.mapToMessiWallpaper(wallpaper))
            )
        }
    }

    private fun mapFlowToViewUI(flow: Flow<PagingData<MessiWallpaper>>): Resource<Flow<PagingData<WallpaperUI>>> {
        return Resource.success(
            flow.map { pagingData ->
                pagingData.map { wallpaper ->
                    wallpaperUIMapper.mapToViewUI(wallpaper)
                }
            }
        )
    }

    enum class Section {
        POPULAR, BARCA, PSG, ARGENTINA
    }
}