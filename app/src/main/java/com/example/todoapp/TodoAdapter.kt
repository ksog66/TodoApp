package com.example.todoapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.ListItemBinding
import com.example.todoapp.db.TodoList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodoAdapter(
    val setNotification: (time:Long,todoTask:String) ->Unit
):RecyclerView.Adapter<TodoAdapter.TaskViewHolder>() {

    private val allTodo = ArrayList<TodoList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemBinding.inflate(
            parent.context.getSystemService(LayoutInflater::class.java),
            parent,
            false
        )
        return TaskViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val singleTodo = allTodo[position]
        ListItemBinding.bind(holder.itemView).apply {
            val colors = root.resources.getIntArray(R.array.random_color)
            val randomColor = colors[Random().nextInt(colors.size)]
            viewColorTag.setBackgroundColor(randomColor)
            titleTv.text = singleTodo.title
            showTaskTv.text = singleTodo.description
            categoryTv.text = singleTodo.category
            timeTv.text  = SimpleDateFormat("h:mm a").format(Date(singleTodo.time))
            dateTv.text = SimpleDateFormat("EEE, d MMM yyyy").format(Date(singleTodo.date))
            setNotification(singleTodo.time,singleTodo.title)
        }
    }

    override fun getItemCount(): Int = allTodo.size

    override fun getItemId(position: Int): Long {
        return allTodo[position].id
    }

    inner class TaskViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)

    fun updateList(todoList:List<TodoList>){
        allTodo.clear()
        allTodo.addAll(todoList)
        allTodo.sortedByDescending { it.time }
        notifyDataSetChanged()
    }
}