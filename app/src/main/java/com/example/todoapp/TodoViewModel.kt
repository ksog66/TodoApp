package com.example.todoapp

import android.app.Application
import androidx.lifecycle.*
import com.example.todoapp.db.NoteList
import com.example.todoapp.db.TodoDatabase
import com.example.todoapp.db.TodoList
import com.example.todoapp.db.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoRepository

    val todoList: LiveData<List<NoteList>>

    private val _todo = MutableLiveData<List<TodoList>>()
    val todo: LiveData<List<TodoList>> = _todo

    init {
        val dao = TodoDatabase.getDatabase(application).getDao()
        repository = TodoRepository(dao)
        todoList = repository.getTodoList()
    }

    fun insertNote(
        noteTitle: String,
        priority: Int
    ) = viewModelScope.launch {
        repository.insertNote(
            noteTitle = noteTitle,
            priority = priority
        )
    }

    fun insertTodo(
        taskTitle: String,
        title: String,
        description: String,
        category: String,
        date: Long,
        time: Long
    ) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTodo(
            taskTitle = taskTitle,
            title = title,
            description = description,
            category = category,
            date = date,
            time = time,
        )
    }

    fun getTodoItem(title: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.getItemList(title).let {
            _todo.postValue(it)
        }
    }

    fun finishTask(id: Long) = viewModelScope.launch {
        repository.taskCompleted(id)
    }

    fun deleteTask(id: Long) = viewModelScope.launch {
        repository.taskDelete(uid = id)
    }


    fun changePriority(uid: Long) = viewModelScope.launch {

        repository.updatePriority(uid)
    }
}

