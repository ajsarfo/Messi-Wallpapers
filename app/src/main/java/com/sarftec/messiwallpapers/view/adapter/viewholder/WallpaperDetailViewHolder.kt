package com.sarftec.messiwallpapers.view.adapter.viewholder

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sarftec.messiwallpapers.databinding.LayoutWallpaperDetailBinding
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import com.sarftec.messiwallpapers.view.task.Task
import com.sarftec.messiwallpapers.view.task.TaskManager
import com.sarftec.messiwallpapers.view.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import java.util.*

class WallpaperDetailViewHolder<T: BaseViewModel<WallpaperUI.Wallpaper>> private constructor(
    private val layoutBinding: LayoutWallpaperDetailBinding,
    private val dependency: ViewHolderDependency<T>,
) : RecyclerView.ViewHolder(layoutBinding.root) {

    private val id = UUID.randomUUID().toString()

    private fun clearLayout() {
        layoutBinding.image.setImageBitmap(null)
        layoutBinding.image.visibility = View.GONE
        layoutBinding.loadingLayout.visibility = View.VISIBLE
        layoutBinding.loadingSpinner.playAnimation()
    }

    private fun setLayout(resource: Resource<Uri>) {
        layoutBinding.image.visibility = View.VISIBLE
        if (resource.isSuccess()) {
            Glide.with(itemView)
                .load(resource.data!!)
                .addListener(
                    object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.v("TAG", "Error => Glide load failed!")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.v("TAG", "Success => Glide load completed!")
                            dependency.taskManager.removeTask(id)
                            layoutBinding.loadingLayout.visibility = View.GONE
                            layoutBinding.loadingSpinner.pauseAnimation()
                            return false
                        }
                    }
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(layoutBinding.image)
        }
        if (resource.isError()) Log.v("TAG", "${resource.message}")
    }

    fun bind(position: Int, wallpaperUI: WallpaperUI) {
        if (wallpaperUI !is WallpaperUI.Wallpaper) return
        dependency.viewModel.setAtPosition(position, wallpaperUI)
        clearLayout()
        val task = Task.createTask<WallpaperUI.Wallpaper, Resource<Uri>>(
            dependency.coroutineScope,
            wallpaperUI
        )
        task.addExecution { input -> dependency.viewModel.getImage(input) }
        task.addCallback { setLayout(it) }
        dependency.taskManager.addTask(id, task.build())
    }

    companion object {

        fun <T: BaseViewModel<WallpaperUI.Wallpaper>> getInstance(
            parent: ViewGroup,
            dependency: ViewHolderDependency<T>
        ): WallpaperDetailViewHolder<T> {
            val binding = LayoutWallpaperDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return WallpaperDetailViewHolder(binding, dependency)
        }

    }

    class ViewHolderDependency <T : ViewModel>(
        val viewModel: T,
        val coroutineScope: CoroutineScope,
        val taskManager: TaskManager<WallpaperUI.Wallpaper, Resource<Uri>>
    )
}