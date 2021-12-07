package com.sarftec.messiwallpapers.view.fragment_nav

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.sarftec.messiwallpapers.databinding.FragmentQuoteBinding
import com.sarftec.messiwallpapers.view.adapter.QuoteItemAdapter
import com.sarftec.messiwallpapers.view.dialog.SetWallpaperDialog
import com.sarftec.messiwallpapers.view.handler.ToolingHandler
import com.sarftec.messiwallpapers.view.listener.QuoteFragmentListener
import com.sarftec.messiwallpapers.view.utils.downloadGlideImage
import com.sarftec.messiwallpapers.view.utils.toast
import com.sarftec.messiwallpapers.view.viewmodel.QuoteViewModel
import com.sarftec.messiwallpapers.view.viewpager.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuoteFragment : Fragment() {

    private lateinit var layoutBinding: FragmentQuoteBinding

    private lateinit var listener: QuoteFragmentListener

    private val viewModel by viewModels<QuoteViewModel>()

    private val quoteAdapter by lazy {
        QuoteItemAdapter(lifecycleScope, viewModel)
    }

    private val toolingHandler by lazy {
        ToolingHandler(requireContext(), listener.getReadWriteHandler())
    }

    private val wallpaperDialog by lazy {
        SetWallpaperDialog(
            layoutBinding.root,
            onHome = {
                runCurrentBitmapCallback {
                    toolingHandler.setWallpaper(it, ToolingHandler.WallpaperOption.HOME)
                }
            },
            onLock = {
                runCurrentBitmapCallback {
                    toolingHandler.setWallpaper(it, ToolingHandler.WallpaperOption.LOCK)
                }
            }
        )
    }

    private var showNetworkToast = false

    private fun runCurrentBitmapCallback(callback: (Bitmap) -> Unit) {
        listener.showLoadingDialog(true)
        viewModel.getAtPosition(layoutBinding.viewPager.currentItem)?.let { image ->
            lifecycleScope.launch {
                viewModel.getImage(image).let {
                    if (it.isSuccess()) requireActivity().downloadGlideImage(it.data!!)
                        .let { result ->
                            if (result.isSuccess()) {
                                listener.getRewardVideo().showRewardVideo {
                                    listener.showLoadingDialog(false)
                                    callback(result.data!!)
                                }
                            }
                            else {
                                requireContext().toast("Action Failed!")
                                listener.showLoadingDialog(false)
                            }
                        }
                    else {
                        requireContext().toast("Action Failed!")
                        listener.showLoadingDialog(false)
                    }
                    //  if (it.isSuccess()) callback(it.data!!)
                }
            }
        }
    }

    private fun showNetworkError() {
        layoutBinding.apply {
            quoteLayout.visibility = View.GONE
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

    private fun revealLayout(isRevealed: Boolean) {
        layoutBinding.circularProgress.visibility = if (!isRevealed) View.VISIBLE else View.GONE
        layoutBinding.quoteLayout.visibility = if (isRevealed) View.VISIBLE else View.GONE
        layoutBinding.networkErrorLayout.parent.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        if (context is QuoteFragmentListener) listener = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.loadQuotes()
        layoutBinding = FragmentQuoteBinding.inflate(
            layoutInflater,
            container,
            false
        )
        setupViewPager()
        setupButtonListeners()
        viewModel.quoteList.observe(viewLifecycleOwner) {
            if (!viewModel.hasNetwork()) showNetworkError()
            else {
                if (it.isLoading()) revealLayout(false)
                if (it.isSuccess()) {
                    revealLayout(true)
                    quoteAdapter.submitData(it.data!!)
                }
                if (it.isError()) Log.v("TAG", "${it.message}")
            }
        }
        return layoutBinding.root
    }

    private fun setupButtonListeners() {
        layoutBinding.share.setOnClickListener {
            runCurrentBitmapCallback { toolingHandler.shareImage(it) }
        }
        layoutBinding.download.setOnClickListener {
            runCurrentBitmapCallback { toolingHandler.saveImage(it) }
        }
        layoutBinding.wallpaper.setOnClickListener {
            wallpaperDialog.show()
        }
        layoutBinding.networkErrorLayout.reload.setOnClickListener {
            showNetworkToast = true
            viewModel.loadQuotes()
        }
    }

    private fun setupViewPager() {
        layoutBinding.viewPager.adapter = quoteAdapter
        layoutBinding.viewPager.setPageTransformer(
            ZoomOutPageTransformer()
        )
        layoutBinding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    listener.getAdInterstitialManager()?.showAd {}
                }
            }
        )
    }
}