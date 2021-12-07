package com.sarftec.messiwallpapers.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.messiwallpapers.view.adapter.viewholder.WallpaperUploadViewHolder
import com.sarftec.messiwallpapers.view.viewmodel.UploadViewModel
import kotlinx.coroutines.CoroutineScope

class WallpaperUploadAdapter(
    coroutineScope: CoroutineScope,
    viewModel: UploadViewModel
) : RecyclerView.Adapter<WallpaperUploadViewHolder>() {

    private var items: List<UploadViewModel.UploadInfoOverlay> = emptyList()

    private val dependency = WallpaperUploadViewHolder.ViewHolderDependency(
        coroutineScope,
        viewModel
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperUploadViewHolder {
        return WallpaperUploadViewHolder.getInstance(
            parent,
            dependency
        )
    }

    override fun onBindViewHolder(holder: WallpaperUploadViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitData(items: List<UploadViewModel.UploadInfoOverlay>) {
        this.items = items
        notifyDataSetChanged()
    }
}