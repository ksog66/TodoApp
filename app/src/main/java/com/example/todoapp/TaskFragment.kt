package com.example.todoapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.FragmentTaskBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val viewModel: TodoViewModel by activityViewModels()
    private lateinit var todoAdapter: TodoAdapter
    private var taskTitle: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskBinding.inflate(layoutInflater, container, false)

        arguments?.let {
            taskTitle = it.getString(resources.getString(R.string.list_title))
        }
        todoAdapter = TodoAdapter { time, todoTask -> setNotification(time, todoTask) }
        _binding?.itemRv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }

        initSwap()
        viewModel.getTodoItem(taskTitle!!)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.todo.observe({ lifecycle }) {
            todoAdapter.updateList(it)
        }
        _binding?.addItemFab?.setOnClickListener {
            addTask()
        }
    }


    private fun initSwap() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        viewModel.deleteTask(todoAdapter.getItemId(position))
                        viewModel.getTodoItem(taskTitle!!)
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        viewModel.finishTask(todoAdapter.getItemId(position))
                        viewModel.getTodoItem(taskTitle!!)
                    }
                }
                todoAdapter.notifyDataSetChanged()
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val icon: Bitmap

                    if (dX > 0) {

                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_check_white_png)

                        paint.color = Color.parseColor("#388E3C")

                        canvas.drawRect(
                            itemView.left.toFloat(),
                            itemView.top.toFloat(),
                            itemView.left.toFloat() + dX,
                            itemView.bottom.toFloat(),
                            paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat(),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    } else if (dX < 0) {
                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_delete_white_png)

                        paint.color = Color.parseColor("#D32F2F")

                        canvas.drawRect(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat(),
                            paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }
                    viewHolder.itemView.translationX = dX
                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(_binding?.itemRv)
    }

    private fun addTask() {
        findNavController().navigate(
            R.id.action_create_task,
            bundleOf(resources.getString(R.string.list_title) to taskTitle)
        )
    }

    private fun setNotification(time: Long,todoTask: String) {
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "todoChannel",
                "default",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.apply {
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(activity?.baseContext,ReminderBroadcast::class.java)
        intent.putExtra(resources.getString(R.string.list_title),taskTitle)
        intent.putExtra("todoTask",todoTask)
        val pendingIntent = PendingIntent.getBroadcast(activity?.baseContext,1234,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager =  activity?.getSystemService(ALARM_SERVICE ) as AlarmManager

        alarmManager.set(AlarmManager.RTC_WAKEUP,time,pendingIntent)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}