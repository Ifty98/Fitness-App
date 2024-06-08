package com.example.finalversion

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameInput = findViewById<TextView>(R.id.username)
        val passwordInput = findViewById<TextView>(R.id.password)
        val logInButton = findViewById<Button>(R.id.login)

        logInButton.setOnClickListener {
            val enteredUsername = usernameInput.text.toString()
            val enteredPassword = passwordInput.text.toString()

            if (enteredUsername == "" || enteredPassword == "") {
                Toast.makeText(this@MainActivity, "Please enter username and password!!", Toast.LENGTH_LONG).show()
            } else {
                checkClient(enteredUsername, enteredPassword)
            }
        }

        val registrationButton = findViewById<TextView>(R.id.registration)
        registrationButton.movementMethod = LinkMovementMethod.getInstance()

    }

    fun onLinkClick( view: View) {
        val intent = Intent(this, Registration::class.java)
        startActivity(intent)
    }

    private fun checkClient(username: String, password: String) {
        val api = RetrofitClient.retrofit

        api.checkUser(username, password).enqueue(object : Callback<Response1>{
            override fun onResponse(call: Call<Response1>, response: Response<Response1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "User logged succesfully!!", Toast.LENGTH_SHORT).show()
                    getUserID(username, password)
                } else {
                    Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response1>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserID(username: String, password: String) {
        val api = RetrofitClient.retrofit

        api.getID(username, password).enqueue(object : Callback<Response3> {
            override fun onResponse(call: Call<Response3>, response: Response<Response3>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    val id = userResponse?.userId
                    UserManager.userId = id
                    getPersonalData(UserManager.userId.toString())
                } else {
                    Toast.makeText(this@MainActivity, "Id not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Response3>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
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
                        val personalId = firstPersonalData.id

                        UserManager.gender = gender
                        UserManager.age = age
                        UserManager.weight = weight
                        UserManager.personalId = personalId.toString()

                        val intent = Intent(this@MainActivity, Home::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to get personal data!!", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this@MainActivity, "Failed to get personal data!!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PersonalData>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

}


