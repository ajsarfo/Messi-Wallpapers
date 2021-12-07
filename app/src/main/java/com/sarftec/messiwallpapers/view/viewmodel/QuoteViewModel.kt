package com.sarftec.messiwallpapers.view.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sarftec.messiwallpapers.domain.usecase.image.GetImage
import com.sarftec.messiwallpapers.domain.usecase.quote.GetQuotes
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.manager.NetworkManager
import com.sarftec.messiwallpapers.view.mapper.QuoteUIMapper
import com.sarftec.messiwallpapers.view.model.QuoteUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val getQuotes: GetQuotes,
    private val quoteUIMapper: QuoteUIMapper,
    private val networkManager: NetworkManager,
    getImage: GetImage,
) : BaseViewModel<QuoteUI.Quote>(getImage) {

    private val _quoteList = MutableLiveData<Resource<List<QuoteUI>>>()
    val quoteList: LiveData<Resource<List<QuoteUI>>>
        get() = _quoteList

    fun loadQuotes() {
        _quoteList.value = Resource.loading()
        viewModelScope.launch(Dispatchers.Main) {
            val result = getQuotes.execute(GetQuotes.EmptyParam).quotes
            _quoteList.value = if (result.isSuccess()) Resource.success(
                result.data!!.shuffled().map { quoteUIMapper.toQuoteUI(it) }
            ) else Resource.error("${result.message}")
        }
    }

    fun hasNetwork() : Boolean {
        return networkManager.isNetworkAvailable()
    }

    suspend fun getImage(quoteUI: QuoteUI.Quote): Resource<Uri> {
        return getImage.execute(GetImage.GetImageParam(quoteUI.quote.image)).image
    }
}