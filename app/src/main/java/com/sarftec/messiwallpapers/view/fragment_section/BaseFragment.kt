package com.sarftec.messiwallpapers.view.fragment_section

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.sarftec.messiwallpapers.databinding.LayoutWallpaperBaseBinding
import com.sarftec.messiwallpapers.view.adapter.WallpaperItemAdapter
import com.sarftec.messiwallpapers.view.listener.WallpaperFragmentListener
import com.sarftec.messiwallpapers.view.utils.toast
import com.sarftec.messiwallpapers.view.viewmodel.WallpaperViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

abstract class BaseFragment : Fragment() {

    private lateinit var layoutBinding: LayoutWallpaperBaseBinding

    private var listener: WallpaperFragmentListener? = null

    private val viewModel by viewModels<WallpaperViewModel>()

    protected abstract fun getSection(): WallpaperViewModel.Section

    private val wallpaperAdapter by lazy {
        WallpaperItemAdapter(lifecycleScope, viewModel) {
            viewModel.wallpaperClicked(it)
            listener?.navigateToDetail(getSection(), it)
        }
    }

    private var showNetworkToast = false

    override fun onAttach(context: Context) {
        if (context is WallpaperFragmentListener) listener = context
        super.onAttach(context)
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutBinding = LayoutWallpaperBaseBinding.inflate(
            layoutInflater,
            container,
            false
        )
        viewModel.loadWallpapers(getSection())
        setupButtonListeners()
        setupRecyclerView()
        setupLoadingState()
        observeWallpaperFlow()
        return layoutBinding.root
    }

    @InternalCoroutinesApi
    private fun observeWallpaperFlow() {
        viewModel.wallpaperFlow.observe(viewLifecycleOwner) { resources ->
            lifecycleScope.launchWhenCreated {
                if (resources.isSuccess()) resources.data?.collect {
                    wallpaperAdapter.submitData(it)
                }
                if (resources.isError()) Log.v("TAG", "Error => ${resources.message}")
                //if(resources.message!! == NETWORK_ERROR) showNetworkError()
            }
        }
    }

    private fun setupLoadingState() {
        wallpaperAdapter.addLoadStateListener {
            if (viewModel.hasNetwork()) showLayout(it.refresh != LoadState.Loading)
            else showNetworkError()

        }
    }

    private fun setupButtonListeners() {
        layoutBinding.networkErrorLayout.reload.setOnClickListener {
            showNetworkToast = true
            viewModel.loadWallpapers(getSection())
        }
    }

    private fun setupRecyclerView() {
        layoutBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = wallpaperAdapter
        }
    }

    private fun showNetworkError() {
        layoutBinding.apply {
            recyclerView.visibility = View.GONE
            circularProgress.visibility = View.GONE
            networkErrorLayout.parent.visibility = View.VISIBLE
        }
        if (showNetworkToast) {
            showNetworkToast = false
            requireContext().toast(
                "Error retrieving data, please check your internet connection.",
                Toast.LENGTH_SHORT
            )
        }
    }

    private fun showLayout(isShown: Boolean) {
        layoutBinding.recyclerView.visibility = if (isShown) View.VISIBLE else View.GONE
        layoutBinding.circularProgress.visibility = if (isShown) View.GONE else View.VISIBLE
        layoutBinding.networkErrorLayout.parent.visibility = View.GONE
    }
}