package com.sarftec.messiwallpapers.view.adapter

import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.adapter.viewholder.QuoteItemViewHolder
import com.sarftec.messiwallpapers.view.model.QuoteUI
import com.sarftec.messiwallpapers.view.task.TaskManager
import com.sarftec.messiwallpapers.view.viewmodel.QuoteViewModel
import kotlinx.coroutines.CoroutineScope

class QuoteItemAdapter(
    coroutineScope: CoroutineScope,
    viewModel: QuoteViewModel
) : RecyclerView.Adapter<QuoteItemViewHolder>() {

    private var items = listOf<QuoteUI>()

    private val taskManager = TaskManager<QuoteUI.Quote, Resource<Uri>>()

    private val dependency = QuoteItemViewHolder.ViewHolderDependency(
        coroutineScope,
        viewModel,
        taskManager
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteItemViewHolder {
        return QuoteItemViewHolder.getInstance(parent, dependency)
    }

    override fun onBindViewHolder(holder: QuoteItemViewHolder, position: Int) {
        holder.bind(position, items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitData(items: List<QuoteUI>) {
        this.items = items
        notifyDataSetChanged()
    }
}