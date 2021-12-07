package com.sarftec.messiwallpapers.view.fragment_nav

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sarftec.messiwallpapers.R
import com.sarftec.messiwallpapers.databinding.FragmentWallpaperBinding
import com.sarftec.messiwallpapers.view.listener.DrawerFragmentListener
import com.sarftec.messiwallpapers.view.viewpager.WallpaperPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WallpaperFragment : Fragment() {

    private lateinit var layoutBinding: FragmentWallpaperBinding

    private var drawerFragmentListener: DrawerFragmentListener? = null

    override fun onAttach(context: Context) {
        if(context is DrawerFragmentListener) drawerFragmentListener = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutBinding = FragmentWallpaperBinding.inflate(
            layoutInflater,
            container,
            false
        )
        setupViewPager()
        setupTabLayout()
        layoutBinding.toolbar.setNavigationOnClickListener {
            drawerFragmentListener?.openNavDrawer()
        }
        return layoutBinding.root
    }

    private fun setupTabLayout() {
        val tabHeadings = resources.getStringArray(R.array.wallpapers_sections)
        TabLayoutMediator(
            layoutBinding.tabLayout,
            layoutBinding.viewPager
        ) { tab, position ->
            tab.text = tabHeadings[position]
        }.attach()
    }

    private fun setupViewPager() {
        layoutBinding.viewPager.adapter = WallpaperPagerAdapter(requireActivity())
    }
}