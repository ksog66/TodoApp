package com.example.todoapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface TodoDao {


    @Query("Update NoteList Set priority = Case priority when 0 then 1 when 1 then 0 else -1 end where id=:id")
    suspend fun updatePriority(id:Long)

    @Insert()
    suspend fun insertNote(noteList: NoteList):Long

    @Insert()
    suspend fun insertTaskList(itemListModel:TodoList):Long

    @Query("Select * from NoteList")
    fun getNoteList() :LiveData<List<NoteList>>

    @Query("Select * from TodoList where taskListTitle=:title AND isFinished == 0 ")
    fun getItemList(title:String) :List<TodoList>

    @Query("Update TodoList Set isFinished = 1 where id=:id")
    suspend fun finishTask(id:Long)


    @Query("Delete from TodoList where id=:id")
    suspend fun deleteCompletedTask(id:Long)

}