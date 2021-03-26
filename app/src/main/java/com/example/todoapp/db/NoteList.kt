package com.example.todoapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteList(
    var taskTitle:String,
    var priority:Int ,
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
)