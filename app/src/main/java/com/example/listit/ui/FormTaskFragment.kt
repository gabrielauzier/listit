package com.example.listit.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.listit.R
import com.example.listit.databinding.FragmentFormTaskBinding
import com.example.listit.databinding.FragmentHomeBinding
import com.example.listit.helper.FirebaseHelper
import com.example.listit.model.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FormTaskFragment : Fragment() {

    private val args: FormTaskFragmentArgs by navArgs()

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var newTask: Boolean = true
    private var statusTask: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        getArgs()
    }

    private fun getArgs() {
        args.task.let {
            if(it != null) {
                task = it
                configTask()
            }
        }
    }

    private fun configTask() {
        newTask = false
        statusTask = task.status
        binding.txtToolbar.text = "Editing Task"

        binding.edtTaskName.setText(task.title)
        setStatus()
    }

    private fun setStatus() {
        binding.radioGroup.check(
            when(task.status) {
                0 -> {
                    R.id.rbTodo
                }
                1 -> {
                    R.id.rbDoing
                }
                else -> {
                    R.id.rbDone
                }
            }
        )
    }

    private fun initListeners() {
        binding.btnCreateTask.setOnClickListener {
            validateTask()
        }

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, id ->
            statusTask = when (id) {
                R.id.rbTodo -> 0
                R.id.rbDoing -> 1
                else -> 2
            }
        }
    }

    private fun validateTask() {
        val title = binding.edtTaskName.text.toString()

        if(title.isNotEmpty()) {
            binding.progressBar.isVisible = true

            if(newTask) task = Task()
            task.title = title
            task.status = statusTask

            saveTask()

        } else {
            Toast.makeText(requireContext(), "Informe uma descrição para a tarefa.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveTask() {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    if(newTask) {
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "Task successfully saved.", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), "Task successfully updated.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                   Toast.makeText(requireContext(), "An error happened trying to save task.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}