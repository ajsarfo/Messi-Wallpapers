package com.sarftec.messiwallpapers.view.adapter

import android.net.Uri
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.adapter.diffutil.WallpaperItemDiffUtil
import com.sarftec.messiwallpapers.view.adapter.viewholder.WallpaperItemViewHolder
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import com.sarftec.messiwallpapers.view.task.TaskManager
import com.sarftec.messiwallpapers.view.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope

class WallpaperItemAdapter(
    coroutineScope: CoroutineScope,
    viewModel: WallpaperViewModel,
    onClick: (WallpaperUI.Wallpaper) -> Unit
) : PagingDataAdapter<WallpaperUI, WallpaperItemViewHolder>(WallpaperItemDiffUtil) {

    private val taskManager = TaskManager<WallpaperUI.Wallpaper, Resource<Uri>>()


    private val dependency = WallpaperItemViewHolder.ViewHolderDependency(
        viewModel,
        coroutineScope,
        taskManager,
        onClick
    )

    override fun onBindViewHolder(holder: WallpaperItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.viewType ?: -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperItemViewHolder {
        return WallpaperItemViewHolder.getInstance(parent, dependency)
    }
}