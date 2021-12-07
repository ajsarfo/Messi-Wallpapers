package com.sarftec.messiwallpapers.view.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Task<T, U> private constructor(
    private val coroutineScope: CoroutineScope,
    private val input: T
) {
    private var isValid = true

    private var callback: ((U) -> Unit)? = null
    private var execution: (suspend (T) -> U)? = null

    fun invalidate() {
        isValid = false
    }

    fun addCallback(callback: (U) -> Unit) {
        this.callback = callback
    }

    fun addExecution(execution: suspend (T) -> U) {
        this.execution = execution
    }

    fun build(): Task<T, U> {
        coroutineScope.launch {
            execution?.invoke(input)?.let {
                if (isValid) callback?.invoke(it)
            }
        }
        return this
    }

    companion object {
        fun <T, U> createTask(coroutineScope: CoroutineScope, input: T): Task<T, U> {
            return Task(coroutineScope, input)
        }
    }
}