package com.example.listit.ui.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listit.R
import com.example.listit.databinding.FragmentTodoBinding
import com.example.listit.helper.FirebaseHelper
import com.example.listit.model.Task
import com.example.listit.ui.HomeFragmentDirections
import com.example.listit.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TodoFragment : Fragment() {
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private val taskList = mutableListOf<Task>()

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getTasks()
    }

    private fun initClicks() {
        binding.fabAddTask.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFormTaskFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun getTasks() {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {

                        taskList.clear()
                        for(snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task

                            if(task.status == 0) taskList.add(task)
                        }

                        binding.progressBar.isVisible = false
                        binding.textInfo.text = ""

                        taskList.reverse()
                        initAdapter()
                    } else {
                        binding.textInfo.text = "None task registered."
                    }

                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun initAdapter() {
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.setHasFixedSize(true)
        taskAdapter = TaskAdapter(requireContext(), taskList) {task, select ->
            optionSelected(task, select)
        }

        binding.recyclerViewTasks.adapter = taskAdapter
    }

    private fun optionSelected(task: Task, select: Int) {
        when (select) {
            TaskAdapter.SELECT_REMOVE -> {
                deleteTask(task)
            }
            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections.actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_FORWARD -> {
                task.status = 1
                updateTask(task)
            }
        }
    }

    private fun updateTask(task: Task) {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(requireContext(), "Task successfully updated.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "An error happened trying to update task.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
            }
    }

    private fun deleteTask(task: Task) {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .removeValue()

        taskList.remove(task)
        taskAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}