package com.sarftec.messiwallpapers.view.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.sarftec.messiwallpapers.view.model.WallpaperUI

object WallpaperItemDiffUtil : DiffUtil.ItemCallback<WallpaperUI>() {

    override fun areItemsTheSame(oldItem: WallpaperUI, newItem: WallpaperUI): Boolean {
        return if(oldItem is WallpaperUI.Wallpaper && newItem is WallpaperUI.Wallpaper) {
            oldItem.wallpaper.id == newItem.wallpaper.id
        }
        else oldItem.viewType == newItem.viewType
    }

    override fun areContentsTheSame(oldItem: WallpaperUI, newItem: WallpaperUI): Boolean {
        return if(oldItem is WallpaperUI.Wallpaper && newItem is WallpaperUI.Wallpaper) {
            oldItem.wallpaper == newItem.wallpaper
        }
        else oldItem.viewType == newItem.viewType
    }
}