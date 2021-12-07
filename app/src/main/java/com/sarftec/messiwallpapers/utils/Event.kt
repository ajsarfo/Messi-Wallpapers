package com.sarftec.messiwallpapers.utils

class Event<out T>(private val item: T) {

    private var isHandled: Boolean = false

    fun getIfNotHandled() : T? {
        if(isHandled) return null
        isHandled = true
        return item
    }

    fun peek() : T = item
}