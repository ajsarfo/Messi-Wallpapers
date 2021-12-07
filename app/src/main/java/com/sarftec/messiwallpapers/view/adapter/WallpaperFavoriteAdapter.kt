package com.sarftec.messiwallpapers.view.adapter

import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.adapter.viewholder.WallpaperDetailViewHolder
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import com.sarftec.messiwallpapers.view.task.TaskManager
import com.sarftec.messiwallpapers.view.viewmodel.FavoriteViewModel
import kotlinx.coroutines.CoroutineScope

class WallpaperFavoriteAdapter(
    coroutineScope: CoroutineScope,
    viewModel: FavoriteViewModel
) : RecyclerView.Adapter<WallpaperDetailViewHolder<FavoriteViewModel>>() {

    private val taskManager = TaskManager<WallpaperUI.Wallpaper, Resource<Uri>>()

    private val dependency = WallpaperDetailViewHolder.ViewHolderDependency(
        viewModel,
        coroutineScope,
        taskManager
    )

    private var items = listOf<WallpaperUI.Wallpaper>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WallpaperDetailViewHolder<FavoriteViewModel> {
        return WallpaperDetailViewHolder.getInstance(parent, dependency)
    }

    override fun onBindViewHolder(
        holder: WallpaperDetailViewHolder<FavoriteViewModel>,
        position: Int
    ) {
        holder.bind(position, items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitData(items: List<WallpaperUI.Wallpaper>) {
        this.items = items
        notifyDataSetChanged()
    }
}