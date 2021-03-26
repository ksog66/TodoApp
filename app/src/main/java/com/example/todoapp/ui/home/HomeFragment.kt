package com.example.todoapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.todoapp.NoteAdapter
import com.example.todoapp.R
import com.example.todoapp.TodoViewModel
import com.example.todoapp.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputLayout

class HomeFragment : Fragment() {

    private val viewModel: TodoViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private lateinit var noteAdapter: NoteAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        noteAdapter = NoteAdapter({ priorityClicked(it) }, { openTaskFragment(it) })
        _binding?.todoRv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = noteAdapter
        }
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.todoList.observe({ lifecycle }) {
            noteAdapter.updateTitle(it)
        }
        _binding?.listFab?.setOnClickListener {
            showCreateDialog()
        }
    }

    private fun showCreateDialog() {
        val dialog = MaterialDialog(requireContext())
            .noAutoDismiss()
            .customView(R.layout.create_list_layout)

        dialog.findViewById<TextView>(R.id.positive_button).setOnClickListener {
            val etTitle =
                dialog.findViewById<TextInputLayout>(R.id.titleInpLay).editText?.text.toString()
            val priority: Int =
                when {
                    dialog.findViewById<RadioButton>(R.id.lowRg).isChecked -> 0
                    dialog.findViewById<RadioButton>(R.id.highRg).isChecked -> 1
                    else -> -1
                }
            when {
                etTitle.isBlank() -> Toast.makeText(
                    requireContext(),
                    "Enter List Title",
                    Toast.LENGTH_SHORT
                ).show()
                priority == -1 -> Toast.makeText(
                    requireContext(),
                    "Select Priority",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    viewModel.insertNote(
                        noteTitle = etTitle,
                        priority = priority
                    )
                }
            }
            dialog.dismiss()
        }

        dialog.findViewById<TextView>(R.id.negative_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun priorityClicked(uid: Long) {
        viewModel.changePriority(uid)
    }

    private fun openTaskFragment(title: String) {
        findNavController().navigate(
            R.id.action_open_task,
            bundleOf(resources.getString(R.string.list_title) to title
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}