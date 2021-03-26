package com.example.todoapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.TaskTitleBinding
import com.example.todoapp.db.NoteList

class NoteAdapter(
    val priorityClicked: (id:Long) -> Unit,
    val openTaskFragment: (title:String) -> Unit) : RecyclerView.Adapter<NoteAdapter.TitleViewHolder>() {

    private var allNoteList = ArrayList<NoteList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val binding = TaskTitleBinding.inflate(
            parent.context.getSystemService(LayoutInflater::class.java),
            parent,
            false
        )
        return TitleViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        val noteList = allNoteList[position]
        TaskTitleBinding.bind(holder.itemView).apply {
            taskTitleTv.text = noteList.taskTitle
            if (noteList.priority == 0) {
                priorityIv.setImageResource(R.drawable.ic_low_priority)
                priorityIv.tag = 0
            } else if (noteList.priority == 1) {
                priorityIv.setImageResource(R.drawable.ic_high_priority)
                priorityIv.tag = 1
            }
            priorityIv.setOnClickListener {
                it.isClickable = false
//                if (it.tag == 0) {
//                    priorityIv.setImageResource(R.drawable.ic_high_priority)
//                    priorityIv.tag = 1
//                } else {
//                    priorityIv.setImageResource(R.drawable.ic_low_priority)
//                    priorityIv.tag = 0
//                }
                priorityClicked(noteList.id)
                it.isClickable = true

            }
            root.setOnClickListener{
                openTaskFragment(noteList.taskTitle)
            }
        }
    }

    override fun getItemCount(): Int = allNoteList.size

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun updateTitle(noteList: List<NoteList>) {
        allNoteList.clear()
        allNoteList.addAll(noteList)
        allNoteList.sortByDescending {it.priority}
        Log.d("sort", allNoteList.toString())
        notifyDataSetChanged()
    }
}