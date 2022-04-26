package com.example.listit.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.listit.R
import com.example.listit.databinding.ItemAdapterBinding
import com.example.listit.model.Task

class TaskAdapter(
    private val context: Context,
    private val taskList: List<Task>,
    val taskSelected: (Task, Int) -> Unit
): RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

    companion object {
        val SELECT_BACK: Int = 1
        val SELECT_REMOVE: Int = 2
        val SELECT_EDIT: Int = 3
        val SELECT_DETAILS: Int = 4
        val SELECT_FORWARD: Int = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.cardTitle.text = task.title

        holder.binding.btnDelete.setOnClickListener{ taskSelected(task, SELECT_REMOVE) }
        holder.binding.btnEdit.setOnClickListener{ taskSelected(task, SELECT_EDIT) }
        holder.binding.btnDetails.setOnClickListener{ taskSelected(task, SELECT_DETAILS) }

        when(task.status) {
            0 -> {
                // to-do
                holder.binding.imgBtnBack.isVisible = false
                holder.binding.imgBtnForward.setColorFilter(
                    ContextCompat.getColor(context ,R.color.color_doing)
                )
                holder.binding.imgBtnForward.setOnClickListener { taskSelected(task, SELECT_FORWARD) }
            }
            1 -> {
                // doing
                holder.binding.imgBtnBack.setColorFilter(
                    ContextCompat.getColor(context ,R.color.color_todo)
                )
                holder.binding.imgBtnForward.setColorFilter(
                    ContextCompat.getColor(context ,R.color.color_done)
                )
                holder.binding.imgBtnBack.setOnClickListener { taskSelected(task, SELECT_BACK) }
                holder.binding.imgBtnForward.setOnClickListener { taskSelected(task, SELECT_FORWARD) }
            }
            else -> {
                // done
                holder.binding.imgBtnForward.isVisible = false
                holder.binding.imgBtnBack.setColorFilter(
                    ContextCompat.getColor(context ,R.color.color_doing)
                )
                holder.binding.imgBtnBack.setOnClickListener { taskSelected(task, SELECT_BACK) }
            }
        }
    }

    override fun getItemCount() = taskList.size

    class MyViewHolder(val binding: ItemAdapterBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}