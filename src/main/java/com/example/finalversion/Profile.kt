package com.example.finalversion

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {
    private lateinit var displayUsername: TextView
    private lateinit var displayPassword: TextView
    private lateinit var displayAge: TextView
    private lateinit var displayGender: TextView
    private lateinit var displayWeight: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val changeAvatarButton = findViewById<ImageView>(R.id.changeAvatar)
        val imageView = findViewById<ImageView>(R.id.avatar)

        val PREF_SELECTED_IMAGE = "selected_image"

        val avatarImages = arrayOf(R.drawable.emoji1, R.drawable.emoji2, R.drawable.emoji3, R.drawable.emoji4, R.drawable.emoji5)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        var selectedImageId = sharedPreferences.getInt(PREF_SELECTED_IMAGE, avatarImages[0])

        imageView.setImageResource(selectedImageId)

        var currentIndex = avatarImages.indexOf(selectedImageId)

        changeAvatarButton.setOnClickListener {
            currentIndex++

            if (currentIndex >= avatarImages.size) {
                currentIndex = 0
            }

            val newImageId = avatarImages[currentIndex]
            imageView.setImageResource(newImageId)

            sharedPreferences.edit().putInt(PREF_SELECTED_IMAGE, newImageId).apply()
        }

        val id = UserManager.userId.toString()
        val personalId = UserManager.personalId.toString()
        displayUsername = findViewById(R.id.displayUsername)
        displayPassword = findViewById(R.id.displayPassword)
        getUserData(id)

        displayAge = findViewById(R.id.displayAge)
        displayGender = findViewById(R.id.displayGender)
        displayWeight = findViewById(R.id.displayWeight)
        getPersonalData(id)

        val enterPassword = findViewById<TextView>(R.id.inputPassword)
        val savePassword = findViewById<ImageView>(R.id.savePassword)

        savePassword.setOnClickListener {
            val inputPassword = enterPassword.text.toString()
            if (inputPassword == "") {
                Toast.makeText(this@Profile, "Enter a valid password!!", Toast.LENGTH_SHORT).show()
            } else {
                updateUserPassword(id, inputPassword)
            }
        }

        val enterAge = findViewById<TextView>(R.id.ageInput)
        val saveAge = findViewById<ImageView>(R.id.saveAge)

        saveAge.setOnClickListener {
            val inputAge = enterAge.text.toString()
            if (inputAge == "") {
                Toast.makeText(this@Profile, "Enter a valid age!!", Toast.LENGTH_SHORT).show()
            } else {
                updateUserAge(personalId, inputAge)
            }
        }

        val enterWeight = findViewById<TextView>(R.id.weightInput)
        val saveWeight = findViewById<ImageView>(R.id.saveWeight)

        saveWeight.setOnClickListener {
            val inputWeight = enterWeight.text.toString()
            if (inputWeight == "") {
                Toast.makeText(this@Profile, "Enter a valid weight!!", Toast.LENGTH_SHORT).show()
            } else {
                updateUserWeight(personalId, inputWeight)
            }
        }
    }

    private fun getUserData(id: String) {
        val api = RetrofitClient.retrofit

        api.getUser(id).enqueue(object : Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null) {
                        val username = userResponse.username
                        val password = userResponse.password

                        displayUsername.text = username
                        displayPassword.text = password
                    } else {

                    }

                } else {
                    Toast.makeText(this@Profile, "Data not found!!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@Profile, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserPassword(userId: String, newPassword: String) {
        val api = RetrofitClient.retrofit

        api.updateUserPassword(userId, newPassword).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Profile, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Profile,Profile::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API", "Failed to update password: $errorBody")
                    Toast.makeText(this@Profile, "Failed to update password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@Profile, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserAge(id: String, newAge: String) {
        val api = RetrofitClient.retrofit

        api.updateAge(id, newAge).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Profile, "Age updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Profile,Profile::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API", "Failed to update password: $errorBody")
                    Toast.makeText(this@Profile, "Failed to update age", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@Profile, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserWeight(id: String, newWeight: String) {
        val api = RetrofitClient.retrofit

        api.updateWeight(id, newWeight).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Profile, "Weight updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Profile,Profile::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API", "Failed to update password: $errorBody")
                    Toast.makeText(this@Profile, "Failed to update weight", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@Profile, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getPersonalData(userId: String) {
        val api = RetrofitClient.retrofit

        api.getPersonalData(userId).enqueue(object: Callback<List<PersonalData>> {
            override fun onResponse(call: Call<List<PersonalData>>, response: Response<List<PersonalData>>) {
                if (response.isSuccessful) {
                    val personalDataList = response.body()
                    if (personalDataList != null && personalDataList.isNotEmpty()) {
                        val firstPersonalData = personalDataList[0]
                        val gender = firstPersonalData.gender
                        val age = firstPersonalData.age
                        val weight = firstPersonalData.weight

                        displayAge.text = age
                        displayGender.text = gender
                        displayWeight.text = weight
                    } else {
                        Toast.makeText(this@Profile, "Failed to get personal data!!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Profile, "Failed to get personal data!!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PersonalData>>, t: Throwable) {
                Toast.makeText(this@Profile, "Network Error", Toast.LENGTH_SHORT).show()
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
}