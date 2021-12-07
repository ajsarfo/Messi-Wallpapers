package com.sarftec.messiwallpapers.view.model

import com.sarftec.messiwallpapers.domain.model.MessiQuote

sealed class QuoteUI {
    class Quote(val quote: MessiQuote) : QuoteUI()
}