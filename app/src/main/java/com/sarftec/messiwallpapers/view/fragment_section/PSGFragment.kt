package com.sarftec.messiwallpapers.view.fragment_section

import com.sarftec.messiwallpapers.view.viewmodel.WallpaperViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PSGFragment : BaseFragment() {

    override fun getSection(): WallpaperViewModel.Section {
        return WallpaperViewModel.Section.PSG
    }
}