package com.sarftec.messiwallpapers.view.mapper

import com.sarftec.messiwallpapers.domain.model.MessiQuote
import com.sarftec.messiwallpapers.view.model.QuoteUI
import javax.inject.Inject

class QuoteUIMapper @Inject constructor(){

    fun toQuoteUI(quote: MessiQuote) : QuoteUI {
        return QuoteUI.Quote(quote)
    }
}