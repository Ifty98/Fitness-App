package com.example.finalversion

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class Home : AppCompatActivity(), SensorEventListener  {

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private lateinit var steps: TextView
    private lateinit var goalSteps: TextView
    private lateinit var time: TextView
    private lateinit var calories: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var studiesProgress: TextView
    private lateinit var academicProgressBar: ProgressBar
    private lateinit var healthProgressBar: ProgressBar
    private lateinit var healthProgress: TextView
    //private val handler = Handler(Looper.getMainLooper())

    private var isWalking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val id = UserManager.userId

        steps = findViewById(R.id.steps)
        goalSteps = findViewById(R.id.goalSteps)
        time = findViewById(R.id.time)
        calories = findViewById((R.id.calories))
        progressBar = findViewById((R.id.horizontalProgressBar))
        studiesProgress = findViewById(R.id.studiesProgress)
        academicProgressBar = findViewById(R.id.academicProgress)
        healthProgressBar = findViewById(R.id.fitnessProgress)
        healthProgress = findViewById(R.id.healthProgress)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        loadData()

        getProjects(id.toString())
        getActivities(id.toString())
        val finishCounting = findViewById<ImageView>(R.id.resetSteps)
        finishCounting.setOnClickListener {
            Toast.makeText(this@Home, "Long click to reset steps", Toast.LENGTH_SHORT).show()
        }
        finishCounting.setOnLongClickListener {
            resetSteps()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(this, "Sensor not detected!!", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            val actualtSteps = event!!.values[0].toInt()

            if (actualtSteps > 0 && !isWalking) {
                isWalking = true
            } else if (actualtSteps == 0 && isWalking) {
                isWalking = false
            }

            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            goalSteps.text = currentSteps.toString()
            steps.text = currentSteps.toString()
            calculateCaloriesBurned(currentSteps)
            val caloriesBurned = calculateCaloriesBurned(currentSteps)
            calories.text = String.format("%d", caloriesBurned.toInt())
            val percentage = (currentSteps / 10000.0 * 100).toInt()
            progressBar.progress = percentage
            val timeDone = calculateTime(currentSteps)
            time.text = timeDone.toString()
        }
    }

    fun resetSteps() {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val user_id = UserManager.userId.toString()
        val finalSteps = steps.text.toString()
        callStepCounter(user_id, currentDate, finalSteps)
        previousTotalSteps = totalSteps

        totalSteps = 0f
        saveData()
        steps.text = "0"
        goalSteps.text = "0"
        time.text = "0"
        calories.text = "0"
        val intent = Intent(this@Home, Home::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("Home", "$savedNumber")
        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    fun calculateCaloriesBurned(steps: Int): Double {
        return steps * 0.045
    }

    fun calculateWalkingSpeed(ageYears: Int, weightKg: Int): Double {
        val ageFactor = when {
            ageYears < 30 -> 1.0
            ageYears < 60 -> 0.9
            else -> 0.8
        }
        val weightFactor = when {
            weightKg < 50 -> 1.0
            weightKg < 80 -> 0.9
            else -> 0.8
        }

        val baseSpeedMetersPerSecond = 1.4
        return baseSpeedMetersPerSecond * ageFactor * weightFactor
    }


    fun calculateTime(steps: Int): Int {
        val age = UserManager.age?.toIntOrNull() ?: 0
        val weight = UserManager.weight?.toIntOrNull() ?: 0
        val stepLengthFeet = 2.5

        val speed = calculateWalkingSpeed(age, weight)
        val stepLengthMeters = stepLengthFeet / 3.281

        val speedFeetPerSecond = speed * 3.281

        val timeSeconds = (steps * stepLengthMeters) / speedFeetPerSecond

        val timeMinutes = (timeSeconds / 60.0).toInt()

        return timeMinutes
    }



    private fun callStepCounter(userId: String, date: String, steps: String) {
        val api = RetrofitClient.retrofit

        api.createStepCounterEntry(userId, date, steps.toInt()).enqueue(object: Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    val stepCounterResponse = response.body()
                    Toast.makeText(this@Home, "New record saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Home, "Failed to record step counter data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@Home, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getActivities(userId: String) {
        val api = RetrofitClient.retrofit

        api.getActivityEntries(userId).enqueue(object : Callback<List<ActivityData>> {
            override fun onResponse(call: Call<List<ActivityData>>, response: Response<List<ActivityData>>) {
                if (response.isSuccessful) {
                    val activitiesEntries = response.body()
                    getStepCounterEntries(userId, activitiesEntries)
                } else {
                    Toast.makeText(this@Home, "Failed to fetch activities", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ActivityData>>, t: Throwable) {
                Toast.makeText(this@Home, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getStepCounterEntries(userId: String, activities: List<ActivityData>?) {
        val api = RetrofitClient.retrofit

        api.getStepCounterEntries(userId).enqueue(object: Callback<List<StepCounterEntry>> {
            override fun onResponse(call: Call<List<StepCounterEntry>>, response: Response<List<StepCounterEntry>>) {
                if (response.isSuccessful) {
                    val stepCounterEntries = response.body()
                    var over10000StepsCount = 0
                    var between5000And10000StepsCount = 0
                    var lessThan5000StepsCount = 0

                    var weight1 = 0.5
                    var weight2 = 0.3
                    var weight3 = 0.2

                    stepCounterEntries?.forEach { entry ->
                        val steps = entry.steps
                        when {
                            steps > 10000 -> over10000StepsCount++
                            steps in 5000..9999 -> between5000And10000StepsCount++
                            steps < 5000 -> lessThan5000StepsCount++
                        }
                    }

                    activities?.forEach { entry ->
                        val calories = entry.calories_burned.toInt()
                        when {
                            calories > 500 -> over10000StepsCount++
                            calories in 200..499 -> between5000And10000StepsCount++
                            calories < 200 -> lessThan5000StepsCount++
                        }
                    }

                    var total = over10000StepsCount + between5000And10000StepsCount + lessThan5000StepsCount
                    var totalWeightedCount = weight1 * over10000StepsCount + weight2 * between5000And10000StepsCount + weight3* lessThan5000StepsCount

                    var performancePercentage = ((totalWeightedCount / total) * 100).toInt()
                    healthProgress.text = performancePercentage.toString()
                    healthProgressBar.progress = performancePercentage
                } else {
                    Toast.makeText(this@Home, "Failed to fetch step counter entries", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<StepCounterEntry>>, t: Throwable) {
                Toast.makeText(this@Home, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getProjects(userId: String) {
        val api = RetrofitClient.retrofit

        api.getProjects(userId).enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful) {
                    val projects = response.body()

                    var completedCount = 0
                    var inProgressCount = 0
                    var notStartedCount = 0

                    var completedWeight = 0.5
                    var inProgressWeight = 0.4
                    var notStartedWeight = 0.1

                    projects?.forEach { project ->
                        when (project.status) {
                            "Completed" -> completedCount++
                            "In progress" -> inProgressCount++
                            "Not started" -> notStartedCount++
                        }
                    }

                    var total = completedCount + inProgressCount + notStartedCount
                    var totalWeightedCount = completedWeight * completedCount + inProgressWeight * inProgressCount + notStartedWeight * notStartedCount

                    var performancePercentage = ((totalWeightedCount / total) * 100).toInt()
                    studiesProgress.text = performancePercentage.toString()
                    academicProgressBar.progress = performancePercentage

                } else {
                    Toast.makeText(this@Home, "Failed to fetch projects", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Toast.makeText(this@Home, "Network Error", Toast.LENGTH_SHORT).show()
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

    fun onProfilePageClicked(view: View) {
        val intent = Intent(view.context, Profile::class.java)
        startActivity(intent)
    }

}