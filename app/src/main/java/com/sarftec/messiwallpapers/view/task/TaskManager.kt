package com.sarftec.messiwallpapers.view.task

class TaskManager<T, U> {

    private val taskMap = hashMapOf<String, Task<T, U>>()

    fun addTask(id: String, task: Task<T, U>) {
        taskMap.remove(id)?.invalidate()
        taskMap[id] = task
    }

    fun removeTask(id: String) {
        taskMap.remove(id)
    }
}