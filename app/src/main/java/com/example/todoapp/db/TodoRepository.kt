package com.example.todoapp.db

import androidx.lifecycle.LiveData

class TodoRepository(private val todoDao: TodoDao) {

    fun getTodoList() :LiveData<List<NoteList>> = todoDao.getNoteList()

    suspend fun insertNote(
        noteTitle: String,
        priority: Int
    ) {
        todoDao.insertNote(
            NoteList(
                taskTitle = noteTitle,
                priority = priority
            )
        )
    }

    suspend fun insertTodo(
        taskTitle: String,
        title: String,
        description: String,
        category: String,
        date: Long,
        time: Long
    ) {
        todoDao.insertTaskList(
            TodoList(
                taskListTitle = taskTitle,
                title = title,
                description = description,
                category = category,
                date = date,
                time = time,
            )
        )
    }

    suspend fun updatePriority(id:Long) = todoDao.updatePriority(id)

    fun getItemList(title:String) = todoDao.getItemList(title)

    suspend fun taskCompleted(uid: Long) = todoDao.finishTask(uid)

    suspend fun taskDelete(uid: Long) = todoDao.deleteCompletedTask(uid)
}