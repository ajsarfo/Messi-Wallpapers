package com.sarftec.messiwallpapers.view.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.sarftec.messiwallpapers.domain.usecase.header.GetHeader
import com.sarftec.messiwallpapers.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getHeader: GetHeader
) : ViewModel() {

    suspend fun getHeaderImage() : Resource<Uri> {
        return getHeader.execute(GetHeader.HeaderParam).result
    }
}