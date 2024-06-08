package com.example.finalversion

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectAdapter(private val projects: List<Project>) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val projectNameTextView: TextView = itemView.findViewById(R.id.projectNameTextView)
        private val checkButton: Button = itemView.findViewById(R.id.checkButton)

        fun bind(project: Project) {
            projectNameTextView.text = project.name

            checkButton.setOnClickListener {
                val intent = Intent(itemView.context, ProjectDetails::class.java)

                intent.putExtra("projectId", project.id)
                intent.putExtra("projectName", project.name)
                intent.putExtra("projectDescription", project.description)
                intent.putExtra("projectDeadline", project.deadline)
                intent.putExtra("projectStatus", project.status)

                itemView.context.startActivity(intent)
            }
        }
    }
}



