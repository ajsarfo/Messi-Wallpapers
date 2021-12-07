package com.sarftec.messiwallpapers.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sarftec.messiwallpapers.databinding.LayoutUploadItemBinding
import com.sarftec.messiwallpapers.view.viewmodel.UploadViewModel
import kotlinx.coroutines.CoroutineScope

class WallpaperUploadViewHolder private constructor(
    private val layoutBinding: LayoutUploadItemBinding,
    private val dependency: ViewHolderDependency
) : RecyclerView.ViewHolder(layoutBinding.root) {

    fun bind(overlay: UploadViewModel.UploadInfoOverlay) {
        layoutBinding.checkOverlay.visibility = if(overlay.isUploaded) View.VISIBLE else View.GONE
        Glide.with(itemView)
            .load(overlay.imageInfo.uri)
            .into(layoutBinding.image)
    }

    companion object {
        fun getInstance(
            parent: ViewGroup,
            dependency: ViewHolderDependency
        ) : WallpaperUploadViewHolder {

            val binding = LayoutUploadItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return WallpaperUploadViewHolder(binding, dependency)
        }
    }

    class ViewHolderDependency(
        private val coroutineScope: CoroutineScope,
        private val viewModel: UploadViewModel
    )
}