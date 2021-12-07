package com.sarftec.messiwallpapers.utils

class Resource <out T> private constructor(private val status: Status, val data: T?, val message: String?) {

    fun isError() = status == Status.ERROR
    fun isSuccess() = status == Status.SUCCESS
    fun isLoading() = status == Status.LOADING

    private enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {
        fun <U> success (data: U?) : Resource<U> {
            return Resource(Status.SUCCESS, data, null)
        }
        fun <U> error(message: String?, data: U? = null) : Resource<U> {
            return Resource(Status.ERROR, data, message)
        }
        fun <U> loading(data: U? = null) : Resource<U> {
            return Resource(Status.LOADING, data, null)
        }
    }
}