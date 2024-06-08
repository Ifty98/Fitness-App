package com.example.finalversion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val spinner: Spinner = findViewById(R.id.spinner1)
        val options = arrayOf("Male", "Female")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        val textColorWhite = ContextCompat.getColor(this, android.R.color.white)
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (view as? TextView)?.setTextColor(textColorWhite)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        })

        val usernameInput = findViewById<TextView>(R.id.resUsername)
        val passwordInput = findViewById<TextView>(R.id.resPassword)
        val genderSpinner = findViewById<Spinner>(R.id.spinner1)
        val ageInput = findViewById<TextView>(R.id.resAge)
        val weightInput = findViewById<TextView>(R.id.resWeight)

        val resButton = findViewById<Button>(R.id.resButton)

        resButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val gender = genderSpinner.selectedItem.toString()
            val age = ageInput.text.toString()

            val weight = weightInput.text.toString()

            if (username.isEmpty() || password.isEmpty() || gender.isEmpty() || age.isEmpty() || weight.isEmpty()) {
                Toast.makeText(this@Registration, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                checkClient(username, password, gender, age, weight)
            }
        }
    }

    private fun checkClient(username: String, password: String, gender: String, age: String, weight: String) {
        val api = RetrofitClient.retrofit

        api.checkUsername(username, password).enqueue(object : Callback<Response2> {
            override fun onResponse(call: Call<Response2>, response: Response<Response2>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse?.exists == "true") {
                        Toast.makeText(this@Registration, "Please choose a different username", Toast.LENGTH_SHORT).show()
                    } else {
                        createUser(username, password,  gender, age, weight)
                    }
                } else {
                    Toast.makeText(this@Registration, "Failed to check user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response2>, t: Throwable) {
                Toast.makeText(this@Registration, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createUser(username: String, password: String, gender: String, age: String, weight: String) {
        val api = RetrofitClient.retrofit

        api.createUser(username, password).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Registration, "User created successfully", Toast.LENGTH_SHORT).show()
                    getUserID(username, password,  gender, age, weight)

                } else {
                    Toast.makeText(this@Registration, "Failed to create user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@Registration, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserID(username: String, password: String, gender: String, age: String, weight: String) {
        val api = RetrofitClient.retrofit

        api.getID(username, password).enqueue(object : Callback<Response3> {
            override fun onResponse(call: Call<Response3>, response: Response<Response3>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    val id = userResponse?.userId
                    UserManager.userId = id
                    updatePersonalData(UserManager.userId.toString(), gender, age, weight)
                } else {
                    Toast.makeText(this@Registration, "Id not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response3>, t: Throwable) {
                Toast.makeText(this@Registration, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePersonalData(userId: String, gender: String, age: String, weight: String) {
        val api = RetrofitClient.retrofit

        val requestData = UpdatePersonalDataRequest(gender, age, weight)

        api.updatePersonalData(userId, requestData).enqueue(object : Callback<Response1> {
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    getPersonalData(UserManager.userId.toString())
                    val intent = Intent(this@Registration, Home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@Registration, "Failed to update personal data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@Registration, "Network Error", Toast.LENGTH_SHORT).show()
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

                        UserManager.gender = gender
                        UserManager.age = age
                        UserManager.weight = weight

                        val intent = Intent(this@Registration, Home::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Registration, "Failed to get personal data!!", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this@Registration, "Failed to get personal data!!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PersonalData>>, t: Throwable) {
                Toast.makeText(this@Registration, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }



}