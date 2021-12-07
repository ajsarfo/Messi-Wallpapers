package com.sarftec.messiwallpapers.domain.repository

import com.sarftec.messiwallpapers.domain.model.MessiQuote
import com.sarftec.messiwallpapers.utils.Resource

interface QuoteRepository {
    suspend fun getMessiQuotes() : Resource<List<MessiQuote>>
}