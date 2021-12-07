package com.sarftec.messiwallpapers.view.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sarftec.messiwallpapers.R
import com.sarftec.messiwallpapers.view.fragment_section.BarcaFragment
import com.sarftec.messiwallpapers.view.fragment_section.PSGFragment
import com.sarftec.messiwallpapers.view.fragment_section.PopularFragment

class WallpaperPagerAdapter(private val activity: FragmentActivity)
    : FragmentStateAdapter(activity){

    override fun getItemCount(): Int {
        return activity.resources.getStringArray(R.array.wallpapers_sections).size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> PopularFragment()
            1 -> BarcaFragment()
            else -> PSGFragment()
        }
    }
}