package com.sarftec.messiwallpapers.view.parcel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class WallpaperToDetail(
    val wallpaperId: Long,
    val section: Int
) : Parcelable {

    companion object {
        const val POPULAR = 0
        const val BARCA = 1
        const val PSG = 2
        const val ARGENTINA = 3
    }
}