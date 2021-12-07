package com.sarftec.messiwallpapers.view.adapter.viewholder

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sarftec.messiwallpapers.databinding.LayoutWallpaperItemBinding
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import com.sarftec.messiwallpapers.view.task.Task
import com.sarftec.messiwallpapers.view.task.TaskManager
import com.sarftec.messiwallpapers.view.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope
import java.util.*

class WallpaperItemViewHolder(
    private val layoutBinding: LayoutWallpaperItemBinding,
    private val dependency: ViewHolderDependency
) : RecyclerView.ViewHolder(layoutBinding.root) {

    private val uuid = UUID.randomUUID().toString()

    private fun setLayout(resource: Resource<Uri>) {
        if (resource.isSuccess()) {
            /*
             layoutBinding.apply {
                 shimmerLayout.stopShimmer()
                 shimmerLayout.visibility = View.GONE
             }
             */
            layoutBinding.contentLayout.visibility = View.VISIBLE
            Glide.with(itemView)
                .load(resource.data!!)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(layoutBinding.image)
        }
        if (resource.isError()) Log.v("TAG", "${resource.message}")
        dependency.taskManager.removeTask(uuid)
    }

    private fun clearLayout(wallpaperUI: WallpaperUI.Wallpaper) {
        layoutBinding.views.text = formatLikesAndViews(wallpaperUI.wallpaper.views)
        layoutBinding.wallpaperCard.setOnClickListener {
            dependency.onClick(wallpaperUI)
        }
        layoutBinding.image.setImageBitmap(null)
        layoutBinding.contentLayout.visibility = View.GONE
        /*
        layoutBinding.shimmerLayout.apply {
            visibility = View.VISIBLE
            startShimmer()
        }
         */
    }

    fun bind(wallpaperUI: WallpaperUI) {
        if (wallpaperUI !is WallpaperUI.Wallpaper) return
        clearLayout(wallpaperUI)
          val task = Task.createTask<WallpaperUI.Wallpaper, Resource<Uri>>(
              dependency.coroutineScope,
              wallpaperUI
          )
          task.addExecution { input -> dependency.viewModel.getImage(input) }
          task.addCallback { setLayout(it) }
          dependency.taskManager.addTask(uuid, task.build())
    }

    private fun formatLikesAndViews(value: Long): String {
        return if (value > 1000) String.format("%.1fk", value.toDouble().div(1000.0))
        else value.toString()
    }

    companion object {
        fun getInstance(
            parent: ViewGroup,
            dependency: ViewHolderDependency
        ): WallpaperItemViewHolder {
            val binding = LayoutWallpaperItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return WallpaperItemViewHolder(binding, dependency)
        }
    }

    class ViewHolderDependency(
        val viewModel: WallpaperViewModel,
        val coroutineScope: CoroutineScope,
        val taskManager: TaskManager<WallpaperUI.Wallpaper, Resource<Uri>>,
        val onClick: (WallpaperUI.Wallpaper) -> Unit
    )
}