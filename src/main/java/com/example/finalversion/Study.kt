package com.example.finalversion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Study : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        val id = UserManager.userId.toString()
        val name = findViewById<TextView>(R.id.name)
        val description = findViewById<TextView>(R.id.subtaskName)
        val deadline = findViewById<TextView>(R.id.deadline)
        val newProjectButton = findViewById<Button>(R.id.add_project)
        val status = "In progress"

        newProjectButton.setOnClickListener {
            val enteredName = name.text.toString()
            val enteredDescription = description.text.toString()
            val enteredDeadline = deadline.text.toString()

            if (enteredName == "" || enteredDescription == "" || enteredDeadline == "") {
                Toast.makeText(this@Study, "Please fill up all the fields!!", Toast.LENGTH_LONG).show()
            } else {
                createProject(id, enteredName, enteredDescription, enteredDeadline, status)
            }
        }

        getProjects(id)

    }

    private fun getProjects(userId: String) {
        val api = RetrofitClient.retrofit

        api.getProjects(userId).enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful) {
                    val projects = response.body()
                    projects?.let {
                        val recyclerView = findViewById<RecyclerView>(R.id.subtasksView)
                        recyclerView.adapter = ProjectAdapter(it)
                    }
                } else {
                    Toast.makeText(this@Study, "Failed to fetch projects", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Toast.makeText(this@Study, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createProject(userId: String, name: String, description: String, deadline: String, status: String) {
        val api = RetrofitClient.retrofit

        api.createProject(userId, name, description, deadline, status).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Study, "Project created successfully!", Toast.LENGTH_SHORT).show()
                    recreate()
                } else {
                    Toast.makeText(this@Study, "Failed to create project", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@Study, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun onHomePageClicked(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    fun onWorkoutPageClicked(view: View) {
        val intent = Intent(view.context, Workout::class.java)
        startActivity(intent)
    }

    fun onProfilePageClicked(view: View) {
        val intent = Intent(view.context, Profile::class.java)
        startActivity(intent)
    }

}