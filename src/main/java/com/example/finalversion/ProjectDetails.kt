package com.example.finalversion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)

        val projectId = intent.getStringExtra("projectId")
        val projectName = intent.getStringExtra("projectName")
        val projectDescription = intent.getStringExtra("projectDescription")
        val projectDeadline = intent.getStringExtra("projectDeadline")
        val projectStatus = intent.getStringExtra("projectStatus")

        val nameTextView = findViewById<TextView>(R.id.displayName)
        val deadlineTextView = findViewById<TextView>(R.id.displayDeadline)
        val statusTextView = findViewById<TextView>(R.id.displayStatus)
        val descriptionTextView = findViewById<TextView>(R.id.displayDescription)
        val processedProjectId = projectId.toString()

        nameTextView.text = projectName
        deadlineTextView.text = projectDeadline
        statusTextView.text = projectStatus
        descriptionTextView.text = projectDescription

        val spinner: Spinner = findViewById(R.id.statusSpinner)
        val options = arrayOf("Completed", "In progress", "Not started")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.prompt = "Select Status"

        val textColorWhite = ContextCompat.getColor(this, android.R.color.holo_blue_light)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (view as? TextView)?.setTextColor(textColorWhite)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        val newStatus = findViewById<Spinner>(R.id.statusSpinner)
        val saveStatus = findViewById<Button>(R.id.saveStatus)

        saveStatus.setOnClickListener {
            val enteredStatus = newStatus.selectedItem.toString()
            val enteredProjectId = projectId.toString()

            if (enteredStatus.isBlank()) {
                Toast.makeText(this@ProjectDetails, "Please select a status!!", Toast.LENGTH_LONG).show()
            } else {
                updateProject(enteredProjectId, enteredStatus)
            }
        }

        val subtaskNameInput = findViewById<TextView>(R.id.subtaskName)
        val addSubtaskButton = findViewById<Button>(R.id.addSubtask)

        addSubtaskButton.setOnClickListener {
            val enteredSubtaskName = subtaskNameInput.text.toString()

            if (enteredSubtaskName == "") {
                Toast.makeText(this@ProjectDetails, "Please enter a name!!", Toast.LENGTH_LONG).show()
            } else {
                createSubtask(processedProjectId, enteredSubtaskName)
            }
        }

        val deleteProject = findViewById<Button>(R.id.deleteProject)

        deleteProject.setOnClickListener {
            Toast.makeText(this@ProjectDetails, "Long press to delete project", Toast.LENGTH_SHORT).show()
        }

        deleteProject.setOnLongClickListener {
            deleteProjectAndSubtasks(processedProjectId)
            true
        }


        getSubtasks(processedProjectId)

    }

    private fun updateProject(projectId: String, status: String) {
        val api = RetrofitClient.retrofit

        api.updateProject(projectId, status).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProjectDetails, "Project updated succesfully!!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProjectDetails, Study::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ProjectDetails, "Failed to update project status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@ProjectDetails, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createSubtask(projectId: String, name: String) {
        val api = RetrofitClient.retrofit
        val status = "Not started"

        api.createSubtask(projectId, name, status).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProjectDetails, "Subtask created!!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProjectDetails, Study::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ProjectDetails, "Failed to create subtask", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@ProjectDetails, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSubtasks(projectId: String) {
        val api = RetrofitClient.retrofit

        api.getSubtasks(projectId).enqueue(object : Callback<List<Subtask>> {
            override fun onResponse(call: Call<List<Subtask>>, response: Response<List<Subtask>>) {
                if (response.isSuccessful) {
                    val subtasks = response.body()
                    subtasks?.let {
                        updateRecyclerView(subtasks)
                    }
                } else {
                    Toast.makeText(this@ProjectDetails, "Failed to fetch subtasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Subtask>>, t: Throwable) {
                Toast.makeText(this@ProjectDetails, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRecyclerView(subtasks: List<Subtask>) {
        val recyclerView: RecyclerView = findViewById(R.id.subtasksView)
        val adapter = SubtaskAdapter(subtasks)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun deleteProjectAndSubtasks(projectId: String) {
        val api = RetrofitClient.retrofit

        api.deleteProjectAndSubtasks(projectId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProjectDetails, "Project and subtasks deleted successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProjectDetails, Study::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@ProjectDetails, "Failed to delete project and subtasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProjectDetails, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun onStudyPageClicked(view: View) {
        val intent = Intent(this, Study::class.java)
        startActivity(intent)
    }

    fun onWorkoutPageClicked(view: View) {
        val intent = Intent(view.context, Workout::class.java)
        startActivity(intent)
    }

    fun onHomePageClicked(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    fun onProfilePageClicked(view: View) {
        val intent = Intent(view.context, Profile::class.java)
        startActivity(intent)
    }


}