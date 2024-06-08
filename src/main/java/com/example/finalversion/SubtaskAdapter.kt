package com.example.finalversion

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubtaskAdapter(private val subtasks: List<Subtask>) :
    RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder>() {

    inner class SubtaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subtaskNameTextView: TextView = itemView.findViewById(R.id.subtaskNameTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.showStatus)
        val statusSpinner: Spinner = itemView.findViewById(R.id.statusSpinner)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)
        val updateSubtask: Button = itemView.findViewById(R.id.updateSubtask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subtask, parent, false)
        return SubtaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        val currentSubtask = subtasks[position]
        holder.subtaskNameTextView.text = currentSubtask.name
        holder.statusTextView.text = currentSubtask.status
        val subtaskId = currentSubtask.id

        val statusOptions = arrayOf("Not started", "Completed", "In progress")
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.statusSpinner.adapter = adapter

        holder.removeButton.setOnClickListener {
            deleteSubtask(holder.itemView.context ,subtaskId)
        }

        holder.updateSubtask.setOnClickListener {
            val selectedStatus = statusOptions[holder.statusSpinner.selectedItemPosition]
            updateSubtask(holder.itemView.context, subtaskId, selectedStatus)
        }

        holder.statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    override fun getItemCount() = subtasks.size

    private fun updateSubtask(context: Context, subtaskId: String, status: String) {
        val api = RetrofitClient.retrofit

        api.updateSubtask(subtaskId, status).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Subtask status updated successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, Study::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Failed to update subtask status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteSubtask(context: Context, subtaskId: String) {
        val api = RetrofitClient.retrofit

        api.deleteSubtask(subtaskId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Subtask deleted successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, Study::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Failed to delete subtask", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
