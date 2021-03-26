package com.example.todoapp

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.databinding.FragmentAddtaskBinding
import java.text.SimpleDateFormat
import java.util.*


class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddtaskBinding? = null
    private val viewModel: TodoViewModel by activityViewModels()
    private lateinit var myCalendar: Calendar
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    private var finalDate = 0L
    private var finalTime = 0L

    private var taskTitle:String? = null

    private val labels = arrayListOf("Personal", "Business", "Insurance", "Shopping", "Banking")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddtaskBinding.inflate(layoutInflater, container, false)

        arguments?.let {
            taskTitle = it.getString(resources.getString(com.example.todoapp.R.string.list_title))
        }
        setUpSpinner()

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            dateEdt.setOnClickListener {
                setDate()
            }
            timeEdt.setOnClickListener {
                setTime()
            }
            saveBtn.setOnClickListener {
                val title = taskInpLay.editText?.text.toString()
                val description = taskInpLay.editText?.text.toString()
                val category = spinnerCategory.selectedItem.toString()

                if (title.isBlank() || description.isBlank() || category.isBlank() || finalTime == 0L || finalDate == 0L)
                    Toast.makeText(requireContext(), "Fill every Detail", Toast.LENGTH_SHORT).show()
                else {
                    viewModel.insertTodo(
                        taskTitle = taskTitle!!,
                        title = title,
                        description = description,
                        category = category,
                        date = finalDate,
                        time = finalTime
                    )
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun setUpSpinner() {
        val adapter =
            ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_dropdown_item, labels)

        labels.sort()

        _binding?.spinnerCategory?.adapter = adapter
    }

    private fun setDate() {
        myCalendar = Calendar.getInstance()

        dateSetListener =
            DatePickerDialog.OnDateSetListener() { _, year: Int, month: Int, dayOfMonth: Int ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()
            }

        val datePickerDialog = DatePickerDialog(
            requireContext(), dateSetListener, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myFormat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat)
        finalDate = myCalendar.time.time
        _binding?.dateEdt?.setText(sdf.format(myCalendar.time))

        _binding?.timeInptLay?.visibility = View.VISIBLE
    }

    private fun setTime() {

        myCalendar = Calendar.getInstance()

        myCalendar.time = Date(finalDate)
        timeSetListener = TimePickerDialog.OnTimeSetListener() { _, hourOfDay: Int, min: Int ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, min)
            updateTime()
        }

        val timePickerDialog = TimePickerDialog(
            requireContext(), timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE), false
        )
        timePickerDialog
        timePickerDialog.show()
    }

    private fun updateTime() {
        val myformat = "h:mm a"
        val sdf = SimpleDateFormat(myformat)
        finalTime = myCalendar.time.time
        _binding?.timeEdt?.setText(sdf.format(myCalendar.time))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}