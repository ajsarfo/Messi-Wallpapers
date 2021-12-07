package com.sarftec.messiwallpapers.view.adapter

import android.net.Uri
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.adapter.diffutil.WallpaperItemDiffUtil
import com.sarftec.messiwallpapers.view.adapter.viewholder.WallpaperDetailViewHolder
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import com.sarftec.messiwallpapers.view.task.TaskManager
import com.sarftec.messiwallpapers.view.viewmodel.DetailViewModel
import kotlinx.coroutines.CoroutineScope

class WallpaperDetailAdapter(
    coroutineScope: CoroutineScope,
    viewModel: DetailViewModel
) : PagingDataAdapter<WallpaperUI, WallpaperDetailViewHolder<DetailViewModel>>(WallpaperItemDiffUtil) {

    private val taskManager = TaskManager<WallpaperUI.Wallpaper, Resource<Uri>>()

    private val dependency = WallpaperDetailViewHolder.ViewHolderDependency(
        viewModel,
        coroutineScope,
        taskManager
    )

    override fun onBindViewHolder(
        holder: WallpaperDetailViewHolder<DetailViewModel>,
        position: Int
    ) {
        getItem(position)?.let {
            holder.bind(position, it)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WallpaperDetailViewHolder<DetailViewModel> {
        return WallpaperDetailViewHolder.getInstance(parent, dependency)
    }
}