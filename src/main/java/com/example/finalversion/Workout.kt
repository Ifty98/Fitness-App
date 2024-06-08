package com.example.finalversion

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class Workout : AppCompatActivity() {
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        val id = UserManager.userId.toString()

        barChart = findViewById(R.id.barChart_view)

        getStepCounterEntries(id)

        val nameInput = findViewById<TextView>(R.id.activityName)
        val timeInput = findViewById<TextView>(R.id.activityTime)
        val caloriesInput = findViewById<TextView>(R.id.activityCalories)
        val sendActivity = findViewById<ImageView>(R.id.sendActivity)

        sendActivity.setOnClickListener {
            val enteredName = nameInput.text.toString()
            val enteredTime = timeInput.text.toString()
            val enteredCalories = caloriesInput.text.toString()

            if (enteredName == "" || enteredTime == "" || enteredCalories == "") {
                Toast.makeText(this@Workout, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                createActivity(id, enteredName, enteredTime, enteredCalories)
            }
        }
    }

    private fun getStepCounterEntries(userId: String) {
        val api = RetrofitClient.retrofit

        api.getStepCounterEntries(userId).enqueue(object: Callback<List<StepCounterEntry>> {
            override fun onResponse(call: Call<List<StepCounterEntry>>, response: Response<List<StepCounterEntry>>) {
                if (response.isSuccessful) {
                    val stepCounterEntries = response.body()
                    if (!stepCounterEntries.isNullOrEmpty()) {
                        val stepsArray = mutableListOf<Int>()
                        stepCounterEntries.takeLast(7).forEach { entry ->
                            stepsArray.add(entry.steps)
                        }
                        Toast.makeText(this@Workout, "Steps retrieved successfully", Toast.LENGTH_SHORT).show()
                        createBarChart(stepsArray)
                    } else {
                        Toast.makeText(this@Workout, "No step counter entries found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Workout, "Failed to fetch step counter entries", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<StepCounterEntry>>, t: Throwable) {
                Toast.makeText(this@Workout, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createBarChart(stepsArray: MutableList<Int>) {
        val entries = stepsArray.mapIndexed { index, steps ->
            BarEntry(index.toFloat(), steps.toFloat())
        }

        val dataSet = BarDataSet(entries, "Steps")

        dataSet.color = Color.rgb(227, 14, 138)
        dataSet.valueTextColor = Color.WHITE

        val data = BarData(dataSet)

        barChart.data = data

        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)

        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Day 1", "Day 2", "Day 3", "Day 4", "Day 5", "Day 6", "Day 7"))
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.textColor = Color.WHITE

        barChart.axisRight.isEnabled = false
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisLeft.setDrawLabels(false)

        barChart.invalidate()
    }

    private fun createActivity(userId: String, name: String, totalTime: String, caloriesBurned: String) {
        val api = RetrofitClient.retrofit

        api.createActivity(userId, name, totalTime, caloriesBurned).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Workout, "Activity created successfully", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API", "Failed to create activity: $errorBody")
                    Toast.makeText(this@Workout, "Failed to create activity", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@Workout, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun onHomePageClicked(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    fun onStudyPageClicked(view: View) {
        val intent = Intent(this, Study::class.java)
        startActivity(intent)
    }

    fun onProfilePageClicked(view: View) {
        val intent = Intent(view.context, Profile::class.java)
        startActivity(intent)
    }

    fun onBMIPageClicked(view: View) {
        val intent = Intent(view.context, BMIpage::class.java)
        startActivity(intent)
    }

}